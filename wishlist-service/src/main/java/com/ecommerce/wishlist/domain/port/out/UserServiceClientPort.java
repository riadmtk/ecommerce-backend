package com.ecommerce.wishlist.domain.port.out;

import java.util.UUID;

public interface UserServiceClientPort {
    String getUserEmail(UUID userId);
}