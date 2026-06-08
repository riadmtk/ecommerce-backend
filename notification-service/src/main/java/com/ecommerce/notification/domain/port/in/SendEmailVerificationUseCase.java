package com.ecommerce.notification.domain.port.in;

public interface SendEmailVerificationUseCase {
    void sendVerificationCode(String email, String code);
}