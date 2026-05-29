package com.ecommerce.notification.domain.port.in;

public interface SendAccountWelcomeUseCase {
    void sendWelcome(String userId, String email, String fullName);
}