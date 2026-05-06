package com.ecommerce.search.infrastructure.adapter.out.elasticsearch.repository;

import com.ecommerce.search.infrastructure.adapter.out.elasticsearch.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataProductRepository extends ElasticsearchRepository<ProductDocument, String> {
    // Only search for active products with stock > 0
    List<ProductDocument> findByNameContainingIgnoreCaseAndActiveTrueAndStockQuantityGreaterThan(String name, int minStock);
}