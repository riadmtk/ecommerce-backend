package com.ecommerce.product.infrastructure.adapter.in.web;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.port.in.*;
import com.ecommerce.product.domain.port.out.StoragePort;
import com.ecommerce.product.infrastructure.adapter.in.web.dto.CreateProductRequest;
import com.ecommerce.product.infrastructure.adapter.in.web.dto.UpdateProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product management APIs for E-commerce")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final GetAllProductsUseCase getAllProductsUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final HardDeleteProductUseCase hardDeleteProductUseCase;
    private final GetAdminProductUseCase getAdminProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final StoragePort storagePort;   // ← à injecter (via @RequiredArgsConstructor)


    // --- UPLOAD IMAGE ---
    @PostMapping("/upload-image")
    @Operation(summary = "Upload an image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = storagePort.store(file);
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }

    @GetMapping("/images/{filename}")
    @Operation(summary = "Récupérer une image produit")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Path filePath = Paths.get("uploads", "products").resolve(filename).normalize();
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL du fichier incorrecte : " + filename, e);
        }

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Fichier introuvable : " + filename);
        }

        String contentType = "application/octet-stream";
        try {
            contentType = Files.probeContentType(filePath);
        } catch (IOException ignored) {
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    // --- POST ---
    @PostMapping
    @Operation(summary = "Create a product", description = "Creates a new active product")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody CreateProductRequest request) {
        // Le Controller fait la traduction : DTO Web -> Command Domaine
        CreateProductCommand command = new CreateProductCommand(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                request.category(),
                request.imageUrl()
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

    // --- GET ALL ---
    @GetMapping
    @Operation(summary = "Get all products (Client)", description = "Retrieves a list of all active products. Returns 204 No Content if the catalog is completely empty.")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = getAllProductsUseCase.getAllProducts();

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(products);
    }

    // --- PUT ---
    @PutMapping("/{id}")
    @Operation(summary = "Update a product")
    public ResponseEntity<Product> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request) {

        // 🟢 Log : valeurs reçues du client
        System.out.println(">>> Category reçue : " + request.category());
        System.out.println(">>> ImageUrl reçue : " + request.imageUrl());

        // Traduction : DTO Web + ID de l'URL -> Command Domaine
        UpdateProductCommand command = new UpdateProductCommand(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                request.category(),   // ajouté
                request.imageUrl()    // ajouté
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