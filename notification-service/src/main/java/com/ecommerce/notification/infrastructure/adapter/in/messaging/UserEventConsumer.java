package com.ecommerce.notification.infrastructure.adapter.in.messaging;

import com.ecommerce.notification.domain.port.in.SendAccountWelcomeUseCase;
import com.ecommerce.notification.domain.port.in.SendPasswordResetUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final SendAccountWelcomeUseCase sendAccountWelcomeUseCase;
    private final SendPasswordResetUseCase sendPasswordResetUseCase;

    // 🎯 CHANGEMENT 1 : Le paramètre est maintenant une Map, plus besoin de String ni d'ObjectMapper
    @KafkaListener(topics = "user.events", groupId = "notification-service-group")
    public void consumeUserEvents(Map<String, Object> event) {
        try {
            String eventType = (String) event.get("eventType");

            // 🎯 CHANGEMENT 2 : On extrait le sous-objet "payload" envoyé par ton User Service
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) event.get("payload");

            if (payload == null) {
                log.warn("⚠️ Received event without payload: {}", eventType);
                return;
            }

            // Note: Ton publisher utilise "UserRegistered", on vérifie donc ce nom exact
            if ("UserRegistered".equals(eventType) || "UserCreated".equals(eventType)) {
                String userId = (String) payload.get("userId");
                String email = (String) payload.get("email");
                // On concatène le prénom et le nom
                String fullName = payload.get("firstName") + " " + payload.get("lastName");

                log.info("📥 Received {} event for User ID: {}", eventType, userId);
                sendAccountWelcomeUseCase.sendWelcome(userId, email, fullName);

            } else if ("PasswordResetRequested".equals(eventType)) {
                String userId = (String) payload.get("userId");
                String email = (String) payload.get("email");
                String resetLink = (String) payload.get("resetLink");

                log.info("📥 Received PasswordResetRequested event for User ID: {}", userId);
                sendPasswordResetUseCase.sendPasswordReset(userId, email, resetLink);
            } else {
                log.info("ℹ️ Event type '{}' ignored (no notification needed).", eventType);
            }

        } catch (Exception e) {
            log.error("❌ Failed to process Kafka User message: {}", e.getMessage(), e);
        }
    }
}