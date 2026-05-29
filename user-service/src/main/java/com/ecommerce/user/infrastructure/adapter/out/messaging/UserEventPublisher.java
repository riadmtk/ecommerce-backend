package com.ecommerce.user.infrastructure.adapter.out.messaging;

import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.port.out.UserEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher implements UserEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.user-registered}")
    private String userRegisteredTopic;

    @Override
    public void publishUserRegistered(User user) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "UserRegistered");
        event.put("timestamp", Instant.now().toString());
        event.put("service", "user-service");
        event.put("version", "1.0");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("userId", user.getId().toString());
        payload.put("email", user.getEmail());
        payload.put("firstName", user.getFirstName());
        payload.put("lastName", user.getLastName());

        event.put("payload", payload);
        kafkaTemplate.send(userRegisteredTopic, user.getId().toString(), event);
        log.info("UserRegistered event sent for userId: {}", user.getId());
    }

    @Override
    public void publishUserLoggedIn(User user) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "UserLoggedIn");
        event.put("timestamp", Instant.now().toString());
        event.put("service", "user-service");
        event.put("version", "1.0");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("userId", user.getId().toString());
        payload.put("email", user.getEmail());

        event.put("payload", payload);
        kafkaTemplate.send(userRegisteredTopic, user.getId().toString(), event);
        log.info("UserLoggedIn event sent for userId: {}", user.getId());
    }

    @Override
    public void publishPasswordResetRequested(User user, String resetLink) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "PasswordResetRequested"); // 👈 Le nom exact attendu par le Notification Service
        event.put("timestamp", Instant.now().toString());
        event.put("service", "user-service");
        event.put("version", "1.0");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("userId", user.getId().toString());
        payload.put("email", user.getEmail());
        payload.put("resetLink", resetLink); // 👈 Le lien sécurisé

        event.put("payload", payload);

        // Note : Tu peux utiliser le même topic ou un autre, on garde userRegisteredTopic pour l'instant si c'est ton topic global "user.events"
        kafkaTemplate.send(userRegisteredTopic, user.getId().toString(), event);
        log.info("PasswordResetRequested event sent for userId: {}", user.getId());
    }
}