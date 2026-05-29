package com.ecommerce.wishlist.infrastructure.adapter.out.messaging;

import com.ecommerce.wishlist.domain.port.out.NotificationEventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaNotificationEventAdapter implements NotificationEventPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topics.wishlist-events}")
    private String topicName;

    @Override
    public void sendRestockNotificationRequest(UUID userId, String userEmail, UUID productId, String productName) {

        // 1. Préparer le payload exact attendu par le Notification Service
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", userId.toString());
        payload.put("userEmail", userEmail);
        payload.put("productId", productId.toString());
        payload.put("productName", productName);

        // 2. Préparer l'enveloppe de l'événement
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "RestockNotificationRequested");
        event.put("payload", payload);

        // 3. Envoyer à Kafka
        log.info("📤 Envoi de l'événement de restockage pour le produit {} à l'utilisateur {}", productName, userId);
        kafkaTemplate.send(topicName, event);
    }
}