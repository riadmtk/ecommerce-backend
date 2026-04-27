package com.ecommerce.cart.domain.port.in;

import java.util.UUID;

public record AddProductToCartCommand(
        UUID userId,
        UUID productId,
        int quantity
) {
    public AddProductToCartCommand {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à zéro");
        }
    }
}