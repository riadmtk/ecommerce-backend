package com.ecommerce.notification.infrastructure.adapter.in.messaging;

import com.ecommerce.notification.domain.port.in.SendRestockAlertUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WishlistEventConsumer {

    private final SendRestockAlertUseCase sendRestockAlertUseCase;

    // 🎯 Listening to a new topic dedicated to wishlist events
    @KafkaListener(
            topics = "${kafka.topics.wishlist-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeWishlistEvents(Map<String, Object> event) {
        try {
            String eventType = (String) event.get("eventType");

            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) event.get("payload");

            if (payload == null) {
                log.warn("⚠️ Received wishlist event without payload: {}", eventType);
                return;
            }

            if ("RestockNotificationRequested".equals(eventType)) {
                String userId = (String) payload.get("userId");
                String email = (String) payload.get("userEmail");
                String productId = (String) payload.get("productId");
                String productName = (String) payload.get("productName");

                log.info("📥 Received RestockNotificationRequested event for Product: {}", productName);
                sendRestockAlertUseCase.sendRestockAlert(userId, email, productId, productName);
            } else {
                log.info("ℹ️ Event type '{}' ignored.", eventType);
            }
        } catch (Exception e) {
            log.error("❌ Failed to process Kafka Wishlist message: {}", e.getMessage(), e);
        }
    }
}