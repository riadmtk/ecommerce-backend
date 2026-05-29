package com.ecommerce.user.domain.port.in;

public interface ExecutePasswordResetUseCase {
    void executeReset(String token, String newPassword);
}