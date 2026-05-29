package com.ecommerce.wishlist.domain.port.in;

import java.util.UUID;

public interface AddProductToWishlistUseCase {
    void addProduct(AddProductCommand command);

    record AddProductCommand(
            UUID userId,
            UUID productId,
            boolean notifyOnRestock
    ) {}
}