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
import java.util.stream.Collectors;

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
    private final StoragePort storagePort;

    @PostMapping(value = "/upload-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload one or multiple images")
    public ResponseEntity<Map<String, List<String>>> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        List<String> uploadedFilenames = files.stream()
                .map(storagePort::store)
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("imageUrls", uploadedFilenames));
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
        } catch (IOException ignored) {}

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @PostMapping
    @Operation(summary = "Create a product")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody CreateProductRequest request) {
        CreateProductCommand command = new CreateProductCommand(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                request.categoryId(), // ← Using ID
                request.imageUrls()
        );

        Product createdProduct = createProductUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product (Client)")
    public ResponseEntity<Product> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(getProductUseCase.getProductById(id));
    }

    @GetMapping("/{id}/admin")
    @Operation(summary = "Get product (Admin)")
    public ResponseEntity<Product> getAdminProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(getAdminProductUseCase.getAdminProductById(id));
    }

    @GetMapping
    @Operation(summary = "Get all products (Client)")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = getAllProductsUseCase.getAllProducts();
        if (products.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product")
    public ResponseEntity<Product> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request) {

        System.out.println(">>> Category ID reçue : " + request.categoryId());
        System.out.println(">>> ImageUrls reçues : " + request.imageUrls());

        UpdateProductCommand command = new UpdateProductCommand(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                request.categoryId(), // ← Using ID
                request.imageUrls()
        );

        Product updatedProduct = updateProductUseCase.execute(command);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a product")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        deleteProductUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    @Operation(summary = "Hard delete a product")
    public ResponseEntity<Void> hardDeleteProduct(@PathVariable UUID id) {
        hardDeleteProductUseCase.hardDeleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock/decrease")
    @Operation(summary = "Decrease product stock")
    public ResponseEntity<Void> decreaseStock(@PathVariable UUID id, @RequestParam int quantity) {
        updateProductStockUseCase.decreaseStock(id, quantity);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock/increase")
    @Operation(summary = "Increase product stock")
    public ResponseEntity<Void> increaseStock(@PathVariable UUID id, @RequestParam int quantity) {
        updateProductStockUseCase.increaseStock(id, quantity);
        return ResponseEntity.noContent().build();
    }
}