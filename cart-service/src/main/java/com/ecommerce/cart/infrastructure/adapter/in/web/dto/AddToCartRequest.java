package com.ecommerce.cart.infrastructure.adapter.in.web.dto;

import java.util.UUID;

public record AddToCartRequest(
        UUID productId,
        int quantity
) {}