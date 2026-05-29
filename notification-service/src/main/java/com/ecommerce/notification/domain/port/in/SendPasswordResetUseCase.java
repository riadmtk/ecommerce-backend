package com.ecommerce.notification.domain.port.in;

public interface SendPasswordResetUseCase {
    void sendPasswordReset(String userId, String email, String resetLink);
}