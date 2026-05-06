package com.ecommerce.product.application.service;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.exception.ResourceNotFoundException;
import com.ecommerce.product.domain.port.in.*;
import com.ecommerce.product.domain.port.out.ProductRepositoryPort;
import com.ecommerce.product.domain.port.out.ProductEventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements CreateProductUseCase, GetProductUseCase, GetAdminProductUseCase, GetAllProductsUseCase, UpdateProductUseCase, DeleteProductUseCase, HardDeleteProductUseCase, UpdateProductStockUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final ProductEventPublisherPort eventPublisherPort;

    // --- POST ---
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
                .category(command.category())   // ajouté
                .imageUrl(command.imageUrl())   // ajouté
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        Product createdProduct = productRepositoryPort.save(newProduct);

        // 🆕 Publication de l’événement "ProductCreated"
        eventPublisherPort.publishProductCreated(createdProduct);

        return createdProduct;
    }

    // --- PUT ---
    @Override
    @Transactional
    public Product execute(UpdateProductCommand command) {
        if (command.price().signum() <= 0) {
            throw new IllegalArgumentException("Product price must be strictly positive.");
        }

        Product existingProduct = getProductById(command.id());

        existingProduct.setName(command.name());
        existingProduct.setDescription(command.description());
        existingProduct.setPrice(command.price());
        existingProduct.setStockQuantity(command.stockQuantity());
        existingProduct.setCategory(command.category());   // ← ajouté
        existingProduct.setImageUrl(command.imageUrl());   // ← ajouté

        // 🟢 Log : valeurs après application des setters
        System.out.println(">>> Category après set : " + existingProduct.getCategory());
        System.out.println(">>> ImageUrl après set : " + existingProduct.getImageUrl());

        Product updatedProduct = productRepositoryPort.save(existingProduct);

        // 🆕 Publication de l’événement "ProductUpdated"
        eventPublisherPort.publishProductUpdated(updatedProduct);

        return updatedProduct;
    }

    // --- GET ADMIN (Interne) ---
    @Override
    @Transactional(readOnly = true)
    public Product getAdminProductById(UUID id) {
        return productRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    // --- GET CLIENT ---
    @Override
    @Transactional(readOnly = true)
    public Product getProductById(UUID id) {
        Product product = productRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        if (!product.isActive()) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        return product;
    }

    // --- GET LIST ---
    @Override
    public List<Product> getAllProducts() {
        return productRepositoryPort.findAllSortedByDate().stream()
                .filter(Product::isActive) // toujours filtrer les inactifs
                .toList();
    }

    // --- SOFT DELETE ---
    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product existingProduct = getProductById(id);
        existingProduct.setActive(false);
        productRepositoryPort.save(existingProduct);
        eventPublisherPort.publishProductDeleted(id);
    }

    // --- HARD DELETE ---
    @Override
    @Transactional
    public void hardDeleteProduct(UUID id) {
        getAdminProductById(id);

        productRepositoryPort.deleteById(id);
        eventPublisherPort.publishProductDeleted(id);
    }

    @Override
    @Transactional
    public void decreaseStock(UUID productId, int quantity) {
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