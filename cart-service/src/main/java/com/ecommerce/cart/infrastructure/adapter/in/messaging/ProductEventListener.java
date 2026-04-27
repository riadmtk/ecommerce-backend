package com.ecommerce.cart.infrastructure.adapter.in.messaging;

import com.ecommerce.cart.domain.port.in.RemoveProductFromAllCartsUseCase;
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
public class ProductEventListener {

    private final RemoveProductFromAllCartsUseCase removeProductFromAllCartsUseCase;
    private final ObjectMapper objectMapper; // Spring Boot auto-provides this

    @KafkaListener(topics = "${kafka.topics.product-events}", groupId = "cart-service-group")
    public void consumeProductEvent(String message) {
        try {
            // Parse the raw JSON string back into the Map you created in the Product Service
            Map<String, Object> event = objectMapper.readValue(message, new TypeReference<>() {});
            String eventType = (String) event.get("eventType");

            if ("ProductDeleted".equals(eventType)) {
                UUID productId = UUID.fromString((String) event.get("productId"));
                log.info("Received ProductDeleted event. Removing product {} from all carts...", productId);

                // Trigger the use case!
                removeProductFromAllCartsUseCase.removeProductFromAllCarts(productId);
            }

        } catch (Exception e) {
            log.error("Failed to process product event: {}", message, e);
        }
    }
}