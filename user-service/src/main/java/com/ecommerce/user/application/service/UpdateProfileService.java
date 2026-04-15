package com.ecommerce.user.application.service;

import com.ecommerce.user.domain.exception.UserNotFoundException;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.port.in.UpdateProfileUseCase;
import com.ecommerce.user.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateProfileService implements UpdateProfileUseCase {

    private final UserRepositoryPort userRepository;

    @Override
    public User updateProfile(UpdateProfileCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        User updatedUser = user.updateProfile(
                command.firstName(),
                command.lastName(),
                command.phoneNumber()
        );

        return userRepository.save(updatedUser);
    }
}