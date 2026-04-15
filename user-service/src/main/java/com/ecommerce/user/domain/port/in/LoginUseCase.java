package com.ecommerce.user.domain.port.in;

public interface LoginUseCase {

    record LoginCommand(
            String email,
            String password
    ) {}

    record LoginResult(
            String token,
            String tokenType,
            long expiresIn
    ) {}

    LoginResult login(LoginCommand command);
}