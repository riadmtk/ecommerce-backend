package com.ecommerce.user.domain.port.in;

public interface RequestPasswordResetUseCase {
    void requestReset(String email);
}