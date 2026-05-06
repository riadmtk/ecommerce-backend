package com.ecommerce.search.infrastructure.adapter.in.web;

import com.ecommerce.search.domain.model.Product;
import com.ecommerce.search.domain.port.in.SearchProductUseCase;
import com.ecommerce.search.infrastructure.adapter.in.web.dto.SearchRequest;
import com.ecommerce.search.infrastructure.adapter.in.web.dto.SearchResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchProductUseCase searchUseCase;

    public SearchController(SearchProductUseCase searchUseCase) {
        this.searchUseCase = searchUseCase;
    }

    @GetMapping("/products")
    public ResponseEntity<SearchResponse> search(SearchRequest request) {

        // 1. Call the Domain use case
        List<Product> domainProducts = searchUseCase.searchProducts(request.getQuery());

        // 2. Map Domain models to Web DTOs
        List<SearchResponse.ProductSummary> summaries = domainProducts.stream()
                .map(product -> SearchResponse.ProductSummary.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .inStock(product.getStockQuantity() > 0)
                        .build())
                .collect(Collectors.toList());

        // 3. Build and return the final response
        SearchResponse response = SearchResponse.builder()
                .results(summaries)
                .totalElements(summaries.size()) // Later, this will come from ES pagination
                .build();

        return ResponseEntity.ok(response);
    }
}