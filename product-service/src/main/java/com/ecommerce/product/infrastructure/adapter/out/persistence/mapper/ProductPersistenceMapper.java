package com.ecommerce.product.infrastructure.adapter.out.persistence.mapper;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceMapper {

    public ProductEntity toEntity(Product product) {
        if (product == null) return null;

        return ProductEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())          // ← doit être présent
                .imageUrl(product.getImageUrl())          // ← doit être présent
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public Product toDomain(ProductEntity entity) {
        if (entity == null) return null;

        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stockQuantity(entity.getStockQuantity())
                .category(entity.getCategory())          // ← doit être présent
                .imageUrl(entity.getImageUrl())          // ← doit être présent
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}