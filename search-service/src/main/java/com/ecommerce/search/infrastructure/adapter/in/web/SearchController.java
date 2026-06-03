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

        List<Product> domainProducts = searchUseCase.searchProducts(request.getQuery());

        List<SearchResponse.ProductSummary> summaries = domainProducts.stream()
                .map(product -> SearchResponse.ProductSummary.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .stockQuantity(product.getStockQuantity())
                        .inStock(product.getStockQuantity() > 0)
                        .categoryId(product.getCategoryId())     // <-- Expose the ID
                        .category(product.getCategoryName())     // <-- Map name to 'category' for UI
                        .imageUrls(product.getImageUrls())
                        .build())
                .collect(Collectors.toList());

        SearchResponse response = SearchResponse.builder()
                .results(summaries)
                .totalElements(summaries.size())
                .build();

        return ResponseEntity.ok(response);
    }
}