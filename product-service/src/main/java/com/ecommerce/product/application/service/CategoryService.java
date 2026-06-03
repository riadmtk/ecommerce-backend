package com.ecommerce.product.application.service;

import com.ecommerce.product.domain.model.Category;
import com.ecommerce.product.domain.port.in.CategoryUseCases;
import com.ecommerce.product.domain.port.out.CategoryRepositoryPort;
import com.ecommerce.product.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryUseCases {

    private final CategoryRepositoryPort categoryRepositoryPort;

    @Override
    @Transactional
    public Category createCategory(String name, String description, UUID parentId) {
        Category newCategory = Category.builder()
                .name(name)
                .description(description)
                .parentId(parentId) // Handles the subcategory logic!
                .build();

        return categoryRepositoryPort.save(newCategory);
    }

    @Override
    @Transactional
    public Category updateCategory(UUID id, String name, String description, UUID parentId) {
        Category existingCategory = categoryRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        existingCategory.setName(name);
        existingCategory.setDescription(description);
        existingCategory.setParentId(parentId);

        return categoryRepositoryPort.save(existingCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepositoryPort.findAll();
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        // Here you might want to check if products are still using this category before deleting!
        categoryRepositoryPort.deleteById(id);
    }
}