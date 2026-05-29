package com.ecommerce.wishlist.domain.port.in;

import com.ecommerce.wishlist.domain.model.Wishlist;
import java.util.UUID;

public interface GetWishlistUseCase {
    Wishlist getWishlist(UUID userId);
}