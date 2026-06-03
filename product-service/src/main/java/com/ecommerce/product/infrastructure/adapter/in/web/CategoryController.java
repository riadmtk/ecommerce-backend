package com.ecommerce.product.infrastructure.adapter.in.web;

import com.ecommerce.product.domain.model.Category;
import com.ecommerce.product.domain.port.in.CategoryUseCases;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category management APIs for E-commerce")
public class CategoryController {

    private final CategoryUseCases categoryUseCases;

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryRequest request) {
        Category created = categoryUseCases.createCategory(
                request.name(),
                request.description(),
                request.parentId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing category")
    public ResponseEntity<Category> updateCategory(
            @PathVariable UUID id,
            @RequestBody CategoryRequest request) {

        Category updated = categoryUseCases.updateCategory(
                id,
                request.name(),
                request.description(),
                request.parentId()
        );
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryUseCases.getAllCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryUseCases.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}

// Simple internal DTO record for the incoming request
record CategoryRequest(String name, String description, UUID parentId) {}