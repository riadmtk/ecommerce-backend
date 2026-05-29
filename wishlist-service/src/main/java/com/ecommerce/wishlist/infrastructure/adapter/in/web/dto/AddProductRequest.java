package com.ecommerce.wishlist.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddProductRequest(
        @NotNull(message = "Product ID is required")
        UUID productId,

        // This is your new feature! If the frontend doesn't send it, it defaults to false.
        boolean notifyOnRestock
) {}