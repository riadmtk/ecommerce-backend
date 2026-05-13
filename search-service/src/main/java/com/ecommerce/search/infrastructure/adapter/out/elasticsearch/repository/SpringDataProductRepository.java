package com.ecommerce.search.infrastructure.adapter.out.elasticsearch.repository;

import com.ecommerce.search.infrastructure.adapter.out.elasticsearch.document.ProductDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataProductRepository extends ElasticsearchRepository<ProductDocument, String> {

    // 🚀 Native Elasticsearch Query
    // - "multi_match": Searches across name, description, and category.
    // - "name^3": Multiplies the score by 3 if the word is found in the name (prioritizes titles).
    // - "fuzziness": "AUTO": Automatically fixes minor typos (e.g., "Gamin Headset").
    // - "filter": Ensures the product is active AND stock is > 0.
    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description\", \"category\"], \"operator\": \"and\", \"fuzziness\": \"AUTO\"}}], \"filter\": [{\"term\": {\"active\": true}}, {\"range\": {\"stockQuantity\": {\"gt\": 0}}}]}}")
    List<ProductDocument> searchProductsWithMultiMatch(String query);
}