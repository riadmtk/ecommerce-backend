package com.ecommerce.product.infrastructure.adapter.in.messaging;

import com.ecommerce.product.domain.port.in.UpdateProductStockUseCase;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.order-events}", groupId = "product-service-group")
    public void handleOrderEvent(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, new TypeReference<>() {});
            String eventType = (String) event.get("eventType");
            if ("OrderCreated".equals(eventType)) {
                // décrémenter stock (déjà implémenté)
                Map<String, Object> payload = (Map<String, Object>) event.get("payload");
                List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
                for (Map<String, Object> item : items) {
                    UUID productId = UUID.fromString((String) item.get("productId"));
                    int quantity = (Integer) item.get("quantity");
                    updateProductStockUseCase.decreaseStock(productId, quantity);
                }
            } else if ("OrderCancelled".equals(eventType)) {
                // augmenter le stock (annulation)
                Map<String, Object> payload = (Map<String, Object>) event.get("payload");
                List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
                for (Map<String, Object> item : items) {
                    UUID productId = UUID.fromString((String) item.get("productId"));
                    int quantity = (Integer) item.get("quantity");
                    updateProductStockUseCase.increaseStock(productId, quantity);
                }
            } else if ("OrderRefunded".equals(eventType)) {
                // augmenter le stock
                Map<String, Object> payload = (Map<String, Object>) event.get("payload");
                List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
                for (Map<String, Object> item : items) {
                    UUID productId = UUID.fromString((String) item.get("productId"));
                    int quantity = (Integer) item.get("quantity");
                    updateProductStockUseCase.increaseStock(productId, quantity);
                }
            }
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement de commande", e);
        }
    }
}