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
                // Extract the user ID who placed the order
                UUID userId = UUID.fromString((String) event.get("userId"));

                log.info("OrderCreated event received for User {}. Clearing their cart...", userId);

                // 3. Trigger the exact same Use Case that the REST API uses!
                clearCartUseCase.clearCart(userId);

                log.info("Cart successfully cleared for User {}.", userId);
            }

        } catch (Exception e) {
            log.error("Failed to process order event to clear cart: {}", message, e);
        }
    }
}