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
                .active(product.isActive())
                // On ne mappe pas les dates ici, Hibernate s'en charge !
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
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt()) // On récupère la date générée par la DB
                .updatedAt(entity.getUpdatedAt()) // On récupère la date de modif générée par la DB
                .build();
    }
}