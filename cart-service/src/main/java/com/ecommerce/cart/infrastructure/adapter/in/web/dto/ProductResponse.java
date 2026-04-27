package com.ecommerce.cart.infrastructure.adapter.in.web.dto;

import java.util.UUID;

public record ProductResponse(
        UUID id,
        int stockQuantity
) {}