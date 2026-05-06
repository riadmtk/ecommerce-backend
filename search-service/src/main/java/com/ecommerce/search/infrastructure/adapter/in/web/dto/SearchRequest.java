package com.ecommerce.search.infrastructure.adapter.in.web.dto;

import lombok.Data;

@Data
public class SearchRequest {
    private String query;

    // Pagination fields with default values
    private int page = 0;
    private int size = 10;

    // You can easily add more filters here later, for example:
    // private BigDecimal minPrice;
    // private BigDecimal maxPrice;
}