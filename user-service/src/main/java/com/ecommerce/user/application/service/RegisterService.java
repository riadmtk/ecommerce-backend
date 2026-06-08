package com.ecommerce.user.application.service;

import com.ecommerce.user.domain.exception.EmailAlreadyExistsException;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.port.in.RegisterUseCase;
import com.ecommerce.user.domain.port.out.PasswordEncoderPort;
import com.ecommerce.user.domain.port.out.UserRepositoryPort;
import com.ecommerce.user.infrastructure.adapter.out.feign.NotificationServiceClient;
import com.ecommerce.user.infrastructure.adapter.out.feign.dto.VerificationCodeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService implements RegisterUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    //private final ApplicationEventPublisher applicationEventPublisher;  // ← remplacer UserEventPublisherPort
    private final NotificationServiceClient notificationClient;

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

        user.generateVerificationCode(15); // 15 minutes

        User savedUser = userRepository.save(user);

        // Envoyer le code par email via notification-service
        try {
            notificationClient.sendVerificationCode(new VerificationCodeRequest(user.getEmail(), user.getVerificationCode()));
        } catch (Exception e) {
            // Log error but don't block registration; user can resend later
            log.error("Failed to send verification email", e);
        }

        // Publier un événement Spring (sera traité après commit de la transaction)
        //applicationEventPublisher.publishEvent(savedUser);

        return savedUser;
    }
}