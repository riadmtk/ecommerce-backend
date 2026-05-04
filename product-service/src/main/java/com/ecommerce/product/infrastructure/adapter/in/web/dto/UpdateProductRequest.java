package com.ecommerce.product.infrastructure.adapter.in.web.dto;


import java.math.BigDecimal;

public record UpdateProductRequest(
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        String category,    // ajouté
        String imageUrl      // ajouté
) {}