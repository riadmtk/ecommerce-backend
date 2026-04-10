package com.ecommerce.user.application.service;

import com.ecommerce.user.domain.exception.EmailAlreadyExistsException;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.port.in.RegisterUseCase;
import com.ecommerce.user.domain.port.out.PasswordEncoderPort;
import com.ecommerce.user.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterService implements RegisterUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;  // ← remplacer UserEventPublisherPort

    @Override
    @Transactional
    public User register(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException(command.email());
        }

        String encodedPassword = passwordEncoder.encode(command.password());

        User user = User.create(
                command.firstName(),
                command.lastName(),
                command.email(),
                encodedPassword
        );

        User savedUser = userRepository.save(user);

        // Publier un événement Spring (sera traité après commit de la transaction)
        applicationEventPublisher.publishEvent(savedUser);

        return savedUser;
    }
}