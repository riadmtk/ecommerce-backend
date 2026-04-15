package com.ecommerce.user.domain.port.in;

import com.ecommerce.user.domain.model.User;

import java.util.UUID;

public interface UpdateProfileUseCase {

    record UpdateProfileCommand(
            UUID userId,
            String firstName,
            String lastName,
            String phoneNumber
    ) {}

    User updateProfile(UpdateProfileCommand command);
}