package com.ecommerce.user.application.service;

import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.port.in.ExecutePasswordResetUseCase;
import com.ecommerce.user.domain.port.in.RequestPasswordResetUseCase;
import com.ecommerce.user.domain.port.out.PasswordEncoderPort;
import com.ecommerce.user.domain.port.out.UserEventPublisherPort;
import com.ecommerce.user.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService implements RequestPasswordResetUseCase, ExecutePasswordResetUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final UserEventPublisherPort userEventPublisher;

    // 🎯 DYNAMIC URL INJECTION
    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Override
    @Transactional
    public void requestReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.requestPasswordReset(token, 15);
            userRepository.save(user);

            // 🎯 CONSTRUCT LINK DYNAMICALLY
            // Ensure this path matches the route you set in app.routes.ts
            String resetLink = frontendUrl + "/auth/reset-password?token=" + token;

            userEventPublisher.publishPasswordResetRequested(user, resetLink);
        });
    }

    @Override
    @Transactional
    public void executeReset(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token."));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.resetPassword(encodedPassword);
        userRepository.save(user);
    }
}