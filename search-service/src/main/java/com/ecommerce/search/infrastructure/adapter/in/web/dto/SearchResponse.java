package com.ecommerce.search.infrastructure.adapter.in.web.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SearchResponse {

    private List<ProductSummary> results;

    // Metadata for the frontend to build pagination UI
    private int totalElements;

    @Data
    @Builder
    public static class ProductSummary {
        private UUID id;
        private String name;
        private String description;
        private BigDecimal price;
        private boolean inStock;
        private String category;
        private List<String> imageUrls;
    }
}