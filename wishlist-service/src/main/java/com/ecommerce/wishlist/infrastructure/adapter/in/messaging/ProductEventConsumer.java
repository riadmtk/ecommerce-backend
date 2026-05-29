package com.ecommerce.wishlist.infrastructure.adapter.in.messaging;

import com.ecommerce.wishlist.application.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final WishlistService wishlistService;

    // On écoute le topic où le product-service annonce ses changements
    @KafkaListener(topics = "product.events", groupId = "wishlist-service-group")
    public void consumeProductEvents(Map<String, Object> event) {
        try {
            String eventType = (String) event.get("eventType");

            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) event.get("payload");

            if ("ProductRestocked".equals(eventType) && payload != null) {
                UUID productId = UUID.fromString((String) payload.get("productId"));
                String productName = (String) payload.get("productName");

                log.info("📥 Événement reçu : Le produit {} est de nouveau en stock !", productName);

                // On lance la machine
                wishlistService.handleProductRestocked(productId, productName);
            }
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement produit", e);
        }
    }
}