package com.ecommerce.wishlist.domain.port.in;

import java.util.UUID;

public interface RemoveProductFromWishlistUseCase {
    void removeProduct(UUID userId, UUID productId);
}