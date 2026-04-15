package com.ecommerce.product.domain.port.in;

import java.math.BigDecimal;

public record CreateProductCommand(
        String name,
        String description,
        BigDecimal price,
        int stockQuantity
) {}