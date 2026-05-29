package com.ecommerce.notification.domain.port.in;

public interface SendRestockAlertUseCase {
    void sendRestockAlert(String userId, String email, String productId, String productName);
}