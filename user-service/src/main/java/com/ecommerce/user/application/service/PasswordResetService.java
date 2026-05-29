package com.ecommerce.user.application.service;

import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.port.in.ExecutePasswordResetUseCase;
import com.ecommerce.user.domain.port.in.RequestPasswordResetUseCase;
import com.ecommerce.user.domain.port.out.PasswordEncoderPort;
import com.ecommerce.user.domain.port.out.UserEventPublisherPort;
import com.ecommerce.user.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService implements RequestPasswordResetUseCase, ExecutePasswordResetUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final UserEventPublisherPort userEventPublisher;

    @Override
    @Transactional
    public void requestReset(String email) {
        // 1. On cherche l'utilisateur. Si non trouvé, on ignore (sécurité contre le scan d'emails)
        userRepository.findByEmail(email).ifPresent(user -> {

            // 2. Générer le token
            String token = UUID.randomUUID().toString();

            // 3. Modifier le modèle (valide 15 minutes)
            user.requestPasswordReset(token, 15);

            // 4. Sauvegarder en DB
            userRepository.save(user);

            // 5. Construire le lien frontend
            String resetLink = "http://localhost:3000/reset-password?token=" + token;

            // 6. Publier directement sur Kafka via ton Port !
            userEventPublisher.publishPasswordResetRequested(user, resetLink);
        });
    }

    @Override
    @Transactional
    public void executeReset(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token."));

        String encodedPassword = passwordEncoder.encode(newPassword);

        // Logique métier (vérifie l'expiration et met à jour)
        user.resetPassword(encodedPassword);

        userRepository.save(user);
    }
}