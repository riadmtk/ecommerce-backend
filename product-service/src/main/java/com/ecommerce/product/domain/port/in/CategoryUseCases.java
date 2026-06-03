package com.ecommerce.product.domain.port.in;

import com.ecommerce.product.domain.model.Category;
import java.util.List;
import java.util.UUID;

public interface CategoryUseCases {
    Category createCategory(String name, String description, UUID parentId);
    Category updateCategory(UUID id, String name, String description, UUID parentId);
    List<Category> getAllCategories();
    void deleteCategory(UUID id);
}