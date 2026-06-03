package com.ecommerce.product.infrastructure.adapter.out.persistence.mapper;

import com.ecommerce.product.domain.model.Category;
import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductImage;
import com.ecommerce.product.infrastructure.adapter.out.persistence.entity.CategoryEntity;
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

        // Map Domain Category to Entity Category
        CategoryEntity categoryEntity = null;
        if (product.getCategory() != null) {
            categoryEntity = CategoryEntity.builder()
                    .id(product.getCategory().getId())
                    .name(product.getCategory().getName())
                    .description(product.getCategory().getDescription())
                    // Note: We usually don't map the parent entity here unless explicitly saving the hierarchy
                    .build();
        }

        ProductEntity entity = ProductEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(categoryEntity) // ← Map the new entity
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .images(new ArrayList<>())
                .build();

        if (product.getImages() != null) {
            for (ProductImage img : product.getImages()) {
                ProductImageEntity imgEntity = ProductImageEntity.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .displayOrder(img.getDisplayOrder())
                        .isPrimary(img.isPrimary())
                        .build();
                entity.addImage(imgEntity);
            }
        }

        return entity;
    }

    public Product toDomain(ProductEntity entity) {
        if (entity == null) return null;

        // Map Entity Category back to Domain Category
        Category domainCategory = null;
        if (entity.getCategory() != null) {
            domainCategory = Category.builder()
                    .id(entity.getCategory().getId())
                    .name(entity.getCategory().getName())
                    .description(entity.getCategory().getDescription())
                    .parentId(entity.getCategory().getParent() != null ? entity.getCategory().getParent().getId() : null)
                    .build();
        }

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
                .category(domainCategory) // ← Map the new domain object
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .images(domainImages)
                .build();
    }
}