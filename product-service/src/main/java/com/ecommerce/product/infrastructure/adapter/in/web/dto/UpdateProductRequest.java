package com.ecommerce.product.infrastructure.adapter.in.web.dto;


import java.math.BigDecimal;
import java.util.List;

public record UpdateProductRequest(
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        String category,
        List<String> imageUrls
) {}