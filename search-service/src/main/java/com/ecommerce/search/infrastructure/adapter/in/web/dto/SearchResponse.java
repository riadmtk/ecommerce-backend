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
    private int totalElements;

    @Data
    @Builder
    public static class ProductSummary {
        private UUID id;
        private String name;
        private String description;
        private BigDecimal price;
        private int stockQuantity;
        private boolean inStock;

        // 🚀 NEW: Add the ID for future filtering
        private UUID categoryId;

        // Keeps the name 'category' so the Angular UI continues working flawlessly
        private String category;

        private List<String> imageUrls;
    }
}