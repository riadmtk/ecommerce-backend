package com.ecommerce.search.infrastructure.adapter.out.elasticsearch;

import com.ecommerce.search.domain.model.Product;
import com.ecommerce.search.domain.port.out.ProductSearchPort;
import com.ecommerce.search.infrastructure.adapter.out.elasticsearch.document.ProductDocument;
import com.ecommerce.search.infrastructure.adapter.out.elasticsearch.repository.SpringDataProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
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

        ProductDocument doc = ProductDocument.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .active(product.isActive())
                .categoryId(product.getCategoryId())     // ← Map ID
                .categoryName(product.getCategoryName()) // ← Map Name
                .imageUrls(product.getImageUrls())
                .build();

        repository.save(doc);
    }

    @Override
    public void removeProduct(String productId) {
        repository.deleteById(productId);
    }

    @Override
    public List<Product> searchByName(String query) {
        return repository.searchProductsWithMultiMatch(query)
                .stream()
                .map(doc -> Product.builder()
                        .id(doc.getId())
                        .name(doc.getName())
                        .description(doc.getDescription())
                        .price(doc.getPrice())
                        .stockQuantity(doc.getStockQuantity())
                        .active(doc.isActive())
                        .categoryId(doc.getCategoryId())     // ← Map ID
                        .categoryName(doc.getCategoryName()) // ← Map Name
                        .imageUrls(doc.getImageUrls())
                        .build())
                .collect(Collectors.toList());
    }
}