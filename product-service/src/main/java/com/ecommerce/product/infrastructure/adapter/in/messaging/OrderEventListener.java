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
    public void handleOrderCreatedEvent(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, new TypeReference<>() {});
            String eventType = (String) event.get("eventType");

            if ("OrderCreated".equals(eventType)) {
                String orderId = (String) event.get("orderId");

                List<Map<String, Object>> items = (List<Map<String, Object>>) event.get("items");

                log.info("Événement Kafka reçu : Nouvelle commande {}. Mise à jour des stocks pour {} articles...", orderId, items.size());

                for (Map<String, Object> item : items) {
                    UUID productId = UUID.fromString((String) item.get("productId"));
                    int quantity = (Integer) item.get("quantity");

                    updateProductStockUseCase.decreaseStock(productId, quantity);
                    log.info("Stock diminué de {} pour le produit {}", quantity, productId);
                }
            }

        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement de commande : {}", message, e);
        }
    }
}