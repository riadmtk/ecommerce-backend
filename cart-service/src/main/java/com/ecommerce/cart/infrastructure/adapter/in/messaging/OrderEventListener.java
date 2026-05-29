package com.ecommerce.cart.infrastructure.adapter.in.messaging;

import com.ecommerce.cart.domain.port.in.ClearCartUseCase;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final ClearCartUseCase clearCartUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.order-events}", groupId = "cart-service-group")
    public void handleOrderCreatedEvent(String message) {
        try {
            // 1. Parse the raw JSON string from the Order Service
            Map<String, Object> event = objectMapper.readValue(message, new TypeReference<>() {});
            String eventType = (String) event.get("eventType");

            // 2. Check if the event is a successful order creation
            if ("OrderCreated".equals(eventType)) {

                // 🎯 CORRECTION : On extrait d'abord le sous-objet "payload"
                @SuppressWarnings("unchecked")
                Map<String, Object> payload = (Map<String, Object>) event.get("payload");

                // On vérifie que le payload et le userId existent bien pour éviter les NullPointerException
                if (payload != null && payload.get("userId") != null) {
                    // On extrait le userId depuis le payload
                    UUID userId = UUID.fromString((String) payload.get("userId"));

                    log.info("📥 OrderCreated event received for User {}. Clearing their cart...", userId);

                    // 3. Trigger the Use Case
                    clearCartUseCase.clearCart(userId);

                    log.info("✅ Cart successfully cleared for User {}.", userId);
                } else {
                    log.warn("⚠️ Received OrderCreated event, but 'payload' or 'userId' is missing!");
                }
            }

        } catch (Exception e) {
            log.error("❌ Failed to process order event to clear cart: {}", message, e);
        }
    }
}