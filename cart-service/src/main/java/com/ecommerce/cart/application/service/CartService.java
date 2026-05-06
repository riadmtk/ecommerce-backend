package com.ecommerce.cart.application.service;

import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.cart.domain.model.CartItem;
import com.ecommerce.cart.domain.port.in.*;
import com.ecommerce.cart.domain.port.out.CartRepositoryPort;
import com.ecommerce.cart.domain.port.out.ProductClientPort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService implements AddProductToCartUseCase, GetCartUseCase, RemoveItemFromCartUseCase, ClearCartUseCase, RemoveProductFromAllCartsUseCase, UpdateCartItemQuantityUseCase {

    private final CartRepositoryPort cartRepositoryPort;
    private final ProductClientPort productClientPort;

    @Override
    public Cart addProductToCart(AddProductToCartCommand command) {
        int availableStock = productClientPort.getAvailableStock(command.productId());

        if (availableStock < command.quantity()) {
            throw new IllegalArgumentException("Stock insuffisant pour ce produit.");
        }

        // 2. Récupération ou création du panier
        Cart cart = cartRepositoryPort.findByUserId(command.userId())
                .orElseGet(() -> Cart.builder().userId(command.userId()).build());

        // 3. Mise à jour des articles
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(command.productId()))
                .findFirst();

        if (existingItem.isPresent()) {
            int newQuantity = existingItem.get().getQuantity() + command.quantity();
            if (availableStock < newQuantity) {
                throw new IllegalArgumentException("Impossible d'ajouter plus. Stock insuffisant.");
            }
            existingItem.get().setQuantity(newQuantity);
        } else {
            cart.getItems().add(CartItem.builder()
                    .productId(command.productId())
                    .quantity(command.quantity())
                    .build());
        }

        cart.setUpdatedAt(LocalDateTime.now());

        return cartRepositoryPort.save(cart);
    }

    @Override
    public Cart getCartByUserId(UUID userId) {
        return cartRepositoryPort.findByUserId(userId)
                .orElseGet(() -> Cart.builder().userId(userId).build());
    }

    @Override
    public Cart removeItem(UUID userId, UUID productId) {
        Cart cart = getCartByUserId(userId);

        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        if (removed) {
            cart.setUpdatedAt(LocalDateTime.now());
            return cartRepositoryPort.save(cart);
        }

        return cart;
    }

    @Override
    public void clearCart(UUID userId) {
        Cart cart = getCartByUserId(userId);

        if (!cart.getItems().isEmpty()) {
            cart.getItems().clear();
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepositoryPort.save(cart);
        }
    }

    @Override
    public void removeProductFromAllCarts(UUID productId) {
        // 1. Find all carts that contain this product
        List<Cart> affectedCarts = cartRepositoryPort.findCartsByProductId(productId);

        // 2. Remove the product from each cart and save
        for (Cart cart : affectedCarts) {
            cart.getItems().removeIf(item -> item.getProductId().equals(productId));
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepositoryPort.save(cart);
        }
    }

    @Override
    public Cart updateItemQuantity(UpdateCartItemQuantityCommand command) {
        Cart cart = cartRepositoryPort.findByUserId(command.userId())
                .orElseThrow(() -> new IllegalStateException("Panier introuvable"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(command.productId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Produit non présent dans le panier"));

        if (command.quantity() <= 0) {
            cart.getItems().remove(item);
        } else {
            int availableStock = productClientPort.getAvailableStock(command.productId());
            if (command.quantity() > availableStock) {
                throw new IllegalArgumentException("Stock insuffisant");
            }
            item.setQuantity(command.quantity());
        }

        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepositoryPort.save(cart);
    }
}