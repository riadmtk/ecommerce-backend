package com.ecommerce.product.domain.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UpdateProductCommand(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        UUID categoryId,
        List<String> imageUrls
) {}