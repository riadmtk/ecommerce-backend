package com.ecommerce.product.application.service;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.port.in.*;
import com.ecommerce.product.domain.port.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements CreateProductUseCase, GetProductUseCase, GetAdminProductUseCase, UpdateProductUseCase, DeleteProductUseCase, HardDeleteProductUseCase, UpdateProductStockUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    // --- POST ---
    // CORRECTION : Renommé de createProduct à execute
    @Override
    @Transactional
    public Product execute(CreateProductCommand command) {
        if (command.price().signum() <= 0) {
            throw new IllegalArgumentException("Product price must be strictly positive.");
        }

        Product newProduct = Product.builder()
                .name(command.name())
                .description(command.description())
                .price(command.price())
                .stockQuantity(command.stockQuantity())
                .active(true) // Mis à jour avec 'active'
                .createdAt(LocalDateTime.now())
                .build();

        return productRepositoryPort.save(newProduct);
    }

    // --- PUT ---
    @Override
    @Transactional
    public Product execute(UpdateProductCommand command) {
        if (command.price().signum() <= 0) {
            throw new IllegalArgumentException("Product price must be strictly positive.");
        }

        Product existingProduct = getAdminProductById(command.id());

        existingProduct.setName(command.name());
        existingProduct.setDescription(command.description());
        existingProduct.setPrice(command.price());
        existingProduct.setStockQuantity(command.stockQuantity());

        return productRepositoryPort.save(existingProduct);
    }

    // --- GET ADMIN (Interne) ---
    @Override
    @Transactional(readOnly = true)
    public Product getAdminProductById(UUID id) {
        return productRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id)); // Modifie l'exception si besoin
    }

    // --- GET CLIENT ---
    @Override
    @Transactional(readOnly = true)
    public Product getProductById(UUID id) {
        Product product = getAdminProductById(id);

        if (!product.isActive()) { // Mis à jour avec 'isActive()'
            throw new RuntimeException("Product not found with ID: " + id);
        }

        return product;
    }

    // --- SOFT DELETE ---
    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        // On utilise l'Admin Get
        Product existingProduct = getAdminProductById(id);

        if (existingProduct.isActive()) {
            existingProduct.setActive(false);
            productRepositoryPort.save(existingProduct);
        }
    }

    // --- HARD DELETE ---
    @Override
    @Transactional
    public void hardDeleteProduct(UUID id) {
        // On vérifie l'existence via l'Admin Get
        getAdminProductById(id);

        productRepositoryPort.deleteById(id);
    }

    @Override
    @Transactional
    public void decreaseStock(UUID productId, int quantity) {
        // L'utilisation de getProductById est parfaite : elle s'assure que le produit est actif
        Product product = getProductById(productId);

        if (product.getStockQuantity() < quantity) {
            throw new IllegalStateException("Insufficient stock for product: " + product.getName());
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);

        productRepositoryPort.save(product);
    }

    @Override
    @Transactional
    public void increaseStock(UUID productId, int quantity) {
        Product product = getProductById(productId);

        product.setStockQuantity(product.getStockQuantity() + quantity);

        productRepositoryPort.save(product);
    }
}