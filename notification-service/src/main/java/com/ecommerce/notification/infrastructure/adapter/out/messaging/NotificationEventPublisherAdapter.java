package com.ecommerce.notification.infrastructure.adapter.out.messaging;

import com.ecommerce.notification.domain.model.Notification;
import com.ecommerce.notification.domain.port.out.NotificationEventPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventPublisherAdapter implements NotificationEventPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper; // 👈 On injecte l'outil de conversion JSON
    private static final String TOPIC = "notification.events";

    @Override
    public void publishNotificationSentEvent(Notification notification) {
        log.info("📤 Publishing NotificationSent event for Reference ID: {}", notification.getReferenceId());
        try {
            // 🎯 On transforme l'objet Java en texte JSON pour que Kafka l'accepte
            String jsonPayload = objectMapper.writeValueAsString(notification);
            kafkaTemplate.send(TOPIC, "NotificationSent", jsonPayload);
        } catch (Exception e) {
            log.error("❌ Failed to serialize Notification object to JSON", e);
        }
    }

    @Override
    public void publishNotificationFailedEvent(Notification notification) {
        log.warn("📤 Publishing NotificationFailed event for Reference ID: {}", notification.getReferenceId());
        try {
            // 🎯 Pareil ici
            String jsonPayload = objectMapper.writeValueAsString(notification);
            kafkaTemplate.send(TOPIC, "NotificationFailed", jsonPayload);
        } catch (Exception e) {
            log.error("❌ Failed to serialize Notification object to JSON", e);
        }
    }
}