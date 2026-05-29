package com.ecommerce.notification.infrastructure.adapter.in.messaging;

import com.ecommerce.notification.domain.port.in.SendOrderConfirmationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final SendOrderConfirmationUseCase sendOrderConfirmationUseCase;

    // 🎯 Changed parameter from String to Map
    @KafkaListener(topics = "order.events", groupId = "notification-service-group")
    public void consumeOrderEvents(Map<String, Object> event) {
        try {
            String eventType = (String) event.get("eventType");

            // 🎯 Extract the nested payload object
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) event.get("payload");

            if (payload == null) {
                log.warn("⚠️ Received order event without payload: {}", eventType);
                return;
            }

            if ("OrderCreated".equals(eventType)) {
                String orderId = (String) payload.get("orderId");

                // Using fallbacks because the Order event currently only contains 'userId'
                String email = (String) payload.getOrDefault("email", "customer@example.com");
                String phone = (String) payload.getOrDefault("phone", "+212600000000");

                log.info("📥 Received OrderCreated event for Order ID: {}", orderId);
                sendOrderConfirmationUseCase.sendConfirmation(orderId, email, phone);
            } else {
                log.info("ℹ️ Event type '{}' ignored.", eventType);
            }
        } catch (Exception e) {
            log.error("❌ Failed to process Kafka Order message: {}", e.getMessage(), e);
        }
    }
}