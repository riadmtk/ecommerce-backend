package com.ecommerce.search.infrastructure.adapter.out.elasticsearch;

import com.ecommerce.search.domain.model.Product;
import com.ecommerce.search.domain.port.out.ProductSearchPort;
import com.ecommerce.search.infrastructure.adapter.out.elasticsearch.document.ProductDocument;
import com.ecommerce.search.infrastructure.adapter.out.elasticsearch.repository.SpringDataProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ProductElasticsearchAdapter implements ProductSearchPort {

    private final SpringDataProductRepository repository;

    public ProductElasticsearchAdapter(SpringDataProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void indexProduct(Product product) {
        if (product == null || product.getId() == null) {
            throw new IllegalArgumentException("Cannot index a product with a null ID");
        }

        // Map Pure Domain -> Infrastructure Document
        ProductDocument doc = ProductDocument.builder()
                .id(product.getId()) // ID is now UUID
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .active(product.isActive())
                .category(product.getCategory())     // ← Map the new category field
                .imageUrls(product.getImageUrls())   // ← Map the new array of images
                .build();

        repository.save(doc);
    }

    @Override
    public void removeProduct(String productId) {
        // Convert the String ID back to UUID before deleting from Elasticsearch
        repository.deleteById(productId);
    }

    @Override
    public List<Product> searchByName(String query) {
        // Fetch from ES and Map Infrastructure Document -> Pure Domain
        return repository.findByNameContainingIgnoreCaseAndActiveTrueAndStockQuantityGreaterThan(query, 0)
                .stream()
                .map(doc -> Product.builder()
                        .id(doc.getId()) // ID is now UUID
                        .name(doc.getName())
                        .description(doc.getDescription())
                        .price(doc.getPrice())
                        .stockQuantity(doc.getStockQuantity())
                        .active(doc.isActive())
                        .category(doc.getCategory())     // ← Map the new category field
                        .imageUrls(doc.getImageUrls())   // ← Map the new array of images
                        .build())
                .collect(Collectors.toList());
    }
}