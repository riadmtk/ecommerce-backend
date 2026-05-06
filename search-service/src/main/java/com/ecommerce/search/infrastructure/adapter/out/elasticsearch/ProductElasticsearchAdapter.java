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
        // Map Domain -> Document
        ProductDocument doc = ProductDocument.builder()
                .id(product.getId().toString())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .active(product.isActive())
                .build();

        repository.save(doc);
    }

    @Override
    public void removeProduct(String productId) {
        repository.deleteById(productId);
    }

    @Override
    public List<Product> searchByName(String query) {
        // Fetch from ES and Map Document -> Domain
        return repository.findByNameContainingIgnoreCaseAndActiveTrueAndStockQuantityGreaterThan(query, 0)
                .stream()
                .map(doc -> Product.builder()
                        .id(UUID.fromString(doc.getId()))
                        .name(doc.getName())
                        .description(doc.getDescription())
                        .price(doc.getPrice())
                        .stockQuantity(doc.getStockQuantity())
                        .active(doc.isActive())
                        .build())
                .collect(Collectors.toList());
    }
}