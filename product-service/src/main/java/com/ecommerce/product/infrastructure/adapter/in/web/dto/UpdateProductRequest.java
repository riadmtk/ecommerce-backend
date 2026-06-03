package com.ecommerce.product.infrastructure.adapter.in.web.dto;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UpdateProductRequest(
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        UUID categoryId,
        List<String> imageUrls
) {}