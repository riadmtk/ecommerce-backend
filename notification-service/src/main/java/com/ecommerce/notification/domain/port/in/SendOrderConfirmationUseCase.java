package com.ecommerce.notification.domain.port.in;

public interface SendOrderConfirmationUseCase {
    void sendConfirmation(String orderId, String email, String phoneNumber);
}