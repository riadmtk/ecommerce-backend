package com.ecommerce.cart.infrastructure.adapter.in.web;

import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.cart.domain.port.in.*;
import com.ecommerce.cart.infrastructure.adapter.in.web.dto.AddToCartRequest;
import com.ecommerce.cart.infrastructure.adapter.in.web.dto.UpdateQuantityRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts/my-cart")
@RequiredArgsConstructor
public class CartController {

    private final AddProductToCartUseCase addProductToCartUseCase;
    private final GetCartUseCase getCartUseCase;
    private final RemoveItemFromCartUseCase removeItemFromCartUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;   // ← ajout


    // --- GET MY CART ---
    @GetMapping
    @Operation(summary = "Obtenir son panier", description = "Récupère le panier de l'utilisateur actuellement connecté.")
    public ResponseEntity<Cart> getMyCart(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = extractUserId(jwt);
        Cart cart = getCartUseCase.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    // --- ADD ITEM TO CART ---
    @PostMapping("/items")
    @Operation(summary = "Ajouter un produit", description = "Ajoute un produit ou augmente sa quantité dans le panier.")
    public ResponseEntity<Cart> addItemToCart(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody AddToCartRequest request) {

        UUID userId = extractUserId(jwt);

        // On construit la commande métier (qui va valider la quantité > 0)
        AddProductToCartCommand command = new AddProductToCartCommand(
                userId,
                request.productId(),
                request.quantity()
        );

        Cart updatedCart = addProductToCartUseCase.addProductToCart(command);
        return ResponseEntity.ok(updatedCart);
    }

    // --- REMOVE ITEM FROM CART ---
    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Retirer un produit", description = "Supprime totalement un produit du panier.")
    public ResponseEntity<Cart> removeItemFromCart(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID productId) {

        UUID userId = extractUserId(jwt);
        Cart updatedCart = removeItemFromCartUseCase.removeItem(userId, productId);
        return ResponseEntity.ok(updatedCart);
    }

    // --- CLEAR ENTIRE CART ---
    @DeleteMapping
    @Operation(summary = "Vider le panier", description = "Supprime tous les articles du panier.")
    public ResponseEntity<Void> clearMyCart(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = extractUserId(jwt);
        clearCartUseCase.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    // --- HELPER METHOD ---
    // Extrait l'ID de l'utilisateur depuis le token JWT de manière centralisée
    private UUID extractUserId(Jwt jwt) {
        String userId = jwt.getSubject();
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("Token JWT invalide : aucun identifiant utilisateur trouvé (sub)");
        }
        return UUID.fromString(userId);
    }

    @PutMapping("/items/{productId}")
    @Operation(summary = "Mettre à jour la quantité d'un article du panier")
    public ResponseEntity<Cart> updateCartItemQuantity(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID productId,
            @RequestBody UpdateQuantityRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());
        UpdateCartItemQuantityUseCase.UpdateCartItemQuantityCommand command =
                new UpdateCartItemQuantityUseCase.UpdateCartItemQuantityCommand(userId, productId, request.quantity());
        Cart updatedCart = updateCartItemQuantityUseCase.updateItemQuantity(command);
        return ResponseEntity.ok(updatedCart);
    }
}