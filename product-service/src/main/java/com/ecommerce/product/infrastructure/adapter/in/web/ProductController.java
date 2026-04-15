package com.ecommerce.product.infrastructure.adapter.in.web;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.port.in.*;
import com.ecommerce.product.infrastructure.adapter.in.web.dto.CreateProductRequest;
import com.ecommerce.product.infrastructure.adapter.in.web.dto.UpdateProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product management APIs for E-commerce")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final HardDeleteProductUseCase hardDeleteProductUseCase;
    private final GetAdminProductUseCase getAdminProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;

    // --- POST ---
    @PostMapping
    @Operation(summary = "Create a product", description = "Creates a new active product")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody CreateProductRequest request) {
        // Le Controller fait la traduction : DTO Web -> Command Domaine
        CreateProductCommand command = new CreateProductCommand(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity()
        );

        Product createdProduct = createProductUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    // --- GET ---
    @GetMapping("/{id}")
    @Operation(summary = "Get product (Client)", description = "Retrieves a product by ID (only if active)")
    public ResponseEntity<Product> getProduct(@PathVariable UUID id) {
        Product product = getProductUseCase.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // --- GET ADMIN ---
    @GetMapping("/{id}/admin")
    @Operation(summary = "Get product (Admin)", description = "Retrieves a product by ID, even if it has been soft-deleted")
    public ResponseEntity<Product> getAdminProduct(@PathVariable UUID id) {
        Product product = getAdminProductUseCase.getAdminProductById(id);
        return ResponseEntity.ok(product);
    }

    // --- PUT ---
    @PutMapping("/{id}")
    @Operation(summary = "Update a product")
    public ResponseEntity<Product> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request) {

        // Traduction : DTO Web + ID de l'URL -> Command Domaine
        UpdateProductCommand command = new UpdateProductCommand(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity()
        );

        Product updatedProduct = updateProductUseCase.execute(command);
        return ResponseEntity.ok(updatedProduct);
    }

    // --- SOFT DELETE ---
    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a product", description = "Marks a product as inactive")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        deleteProductUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // --- HARD DELETE ---
    @DeleteMapping("/{id}/hard")
    @Operation(summary = "Hard delete a product", description = "Permanently removes a product from the database (Admin only)")
    public ResponseEntity<Void> hardDeleteProduct(@PathVariable UUID id) {
        hardDeleteProductUseCase.hardDeleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // --- DIMINUER LE STOCK ---
    @PatchMapping("/{id}/stock/decrease")
    @Operation(summary = "Decrease product stock", description = "Reduces the stock quantity of an active product")
    public ResponseEntity<Void> decreaseStock(
            @PathVariable UUID id,
            @RequestParam int quantity) { // On passe la quantité dans l'URL (ex: ?quantity=2)

        updateProductStockUseCase.decreaseStock(id, quantity);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // --- AUGMENTER LE STOCK ---
    @PatchMapping("/{id}/stock/increase")
    @Operation(summary = "Increase product stock", description = "Adds to the stock quantity of an active product")
    public ResponseEntity<Void> increaseStock(
            @PathVariable UUID id,
            @RequestParam int quantity) {

        updateProductStockUseCase.increaseStock(id, quantity);
        return ResponseEntity.noContent().build();
    }
}