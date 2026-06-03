package com.ecommerce.product.infrastructure.adapter.out.persistence.mapper;

import com.ecommerce.product.domain.model.Category;
import com.ecommerce.product.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryEntity toEntity(Category category) {
        if (category == null) return null;

        // Create a "Proxy" parent entity just to hold the ID for the Foreign Key
        CategoryEntity parentEntity = null;
        if (category.getParentId() != null) {
            parentEntity = CategoryEntity.builder()
                    .id(category.getParentId())
                    .build();
        }

        return CategoryEntity.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parent(parentEntity)
                .build();
    }

    public Category toDomain(CategoryEntity entity) {
        if (entity == null) return null;

        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                // Safely extract the parent ID if a parent exists
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .build();
    }
}