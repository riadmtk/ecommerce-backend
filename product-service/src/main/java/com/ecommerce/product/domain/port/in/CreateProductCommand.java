package com.ecommerce.product.domain.port.in;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductCommand(
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        String category,
        List<String> imageUrls
) {}