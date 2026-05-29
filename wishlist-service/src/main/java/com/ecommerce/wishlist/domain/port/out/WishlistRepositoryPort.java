package com.ecommerce.wishlist.domain.port.out;

import com.ecommerce.wishlist.domain.model.Wishlist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WishlistRepositoryPort {
    Optional<Wishlist> findByUserId(UUID userId);
    Wishlist save(Wishlist wishlist);
    // Needed for Option A (finding who to notify when a restock happens)
    List<Wishlist> findWishlistsNeedingRestockNotification(UUID productId);
}