package com.ecommerce.product.application.service;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductImage;
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
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ProductService implements CreateProductUseCase, GetProductUseCase, GetAdminProductUseCase, GetAllProductsUseCase, UpdateProductUseCase, DeleteProductUseCase, HardDeleteProductUseCase, UpdateProductStockUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final ProductEventPublisherPort eventPublisherPort;

    // --- Helper Method to build Domain Images ---
    private List<ProductImage> buildImages(List<String> imageUrls) {
        List<ProductImage> productImages = new ArrayList<>();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (int i = 0; i < imageUrls.size(); i++) {
                productImages.add(ProductImage.builder()
                        .imageUrl(imageUrls.get(i))
                        .displayOrder(i)
                        .isPrimary(i == 0)
                        .build());
            }
        }
        return productImages;
    }

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
                .category(command.category())
                .images(buildImages(command.imageUrls()))
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        Product createdProduct = productRepositoryPort.save(newProduct);
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

        // 🎯 1. On mémorise l'ancien stock avant la mise à jour
        int oldQuantity = existingProduct.getStockQuantity();

        existingProduct.setName(command.name());
        existingProduct.setDescription(command.description());
        existingProduct.setPrice(command.price());
        existingProduct.setStockQuantity(command.stockQuantity());
        existingProduct.setCategory(command.category());
        existingProduct.setImages(buildImages(command.imageUrls()));

        Product updatedProduct = productRepositoryPort.save(existingProduct);
        eventPublisherPort.publishProductUpdated(updatedProduct);

        // 🎯 2. Si le produit passe de Rupture (0) à En Stock (> 0), on déclenche l'événement !
        if (oldQuantity == 0 && command.stockQuantity() > 0) {
            eventPublisherPort.publishProductRestocked(updatedProduct.getId(), updatedProduct.getName());
        }

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

        // 🎯 1. On mémorise l'ancien stock
        int oldQuantity = product.getStockQuantity();

        product.setStockQuantity(oldQuantity + quantity);
        productRepositoryPort.save(product);

        // 🎯 2. Détection du restockage
        if (oldQuantity == 0 && (oldQuantity + quantity) > 0) {
            eventPublisherPort.publishProductRestocked(productId, product.getName());
        }
    }
}