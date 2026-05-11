package com.ecommerce.product.infrastructure.adapter.out.persistence.mapper;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductImage;
import com.ecommerce.product.infrastructure.adapter.out.persistence.entity.ProductEntity;
import com.ecommerce.product.infrastructure.adapter.out.persistence.entity.ProductImageEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductPersistenceMapper {

    public ProductEntity toEntity(Product product) {
        if (product == null) return null;

        ProductEntity entity = ProductEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .images(new ArrayList<>()) // Initialize empty list
                .build();

        // Safely map domain images to JPA entities using the helper method
        if (product.getImages() != null) {
            for (ProductImage img : product.getImages()) {
                ProductImageEntity imgEntity = ProductImageEntity.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .displayOrder(img.getDisplayOrder())
                        .isPrimary(img.isPrimary())
                        .build();
                entity.addImage(imgEntity); // ← Binds the Foreign Key automatically!
            }
        }

        return entity;
    }

    public Product toDomain(ProductEntity entity) {
        if (entity == null) return null;

        List<ProductImage> domainImages = entity.getImages() != null ?
                entity.getImages().stream().map(img -> ProductImage.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .displayOrder(img.getDisplayOrder())
                        .isPrimary(img.isPrimary())
                        .build()
                ).collect(Collectors.toList()) : new ArrayList<>();

        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stockQuantity(entity.getStockQuantity())
                .category(entity.getCategory())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .images(domainImages)
                .build();
    }
}