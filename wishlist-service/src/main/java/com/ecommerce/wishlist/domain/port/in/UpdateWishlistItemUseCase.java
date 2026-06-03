package com.ecommerce.wishlist.domain.port.in;

import java.util.UUID;

public interface UpdateWishlistItemUseCase {
    void updateRestockNotification(UUID userId, UUID productId, boolean notifyOnRestock);
}