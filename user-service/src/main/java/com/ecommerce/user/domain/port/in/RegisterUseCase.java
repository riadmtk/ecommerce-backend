package com.ecommerce.user.domain.port.in;

import com.ecommerce.user.domain.model.User;

public interface RegisterUseCase {

    record RegisterCommand(
            String firstName,
            String lastName,
            String email,
            String password
    ) {}

    User register(RegisterCommand command);
}