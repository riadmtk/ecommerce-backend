package com.ecommerce.wishlist.domain.port.out;

import java.util.UUID;

public interface NotificationEventPort {
    void sendRestockNotificationRequest(UUID userId, String userEmail, UUID productId, String productName);
}