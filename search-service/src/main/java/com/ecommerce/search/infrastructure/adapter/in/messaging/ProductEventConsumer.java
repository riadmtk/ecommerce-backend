package com.ecommerce.search.infrastructure.adapter.in.messaging;

import com.ecommerce.search.domain.model.Product;
import com.ecommerce.search.domain.port.in.SyncProductUseCase;
import com.ecommerce.search.infrastructure.adapter.in.messaging.dto.ProductEvent;
import lombok.extern.slf4j.Slf4j; // <-- Add this
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j // <-- Add this
@Component
public class ProductEventConsumer {

    private final SyncProductUseCase syncUseCase;

    public ProductEventConsumer(SyncProductUseCase syncUseCase) {
        this.syncUseCase = syncUseCase;
    }

    @KafkaListener(topics = "product.events", groupId = "search-service-group")
    public void consume(ProductEvent event) {

        // --- ADD THIS LOG LINE ---
        log.info("🔔 KAFKA MESSAGE RECEIVED IN SEARCH SERVICE: {}", event);

        if ("DELETED".equalsIgnoreCase(event.getEventType())) {
            syncUseCase.deleteProduct(event.getId().toString());
        } else {
            Product product = Product.builder()
                    .id(event.getId())
                    .name(event.getName())
                    .description(event.getDescription())
                    .price(event.getPrice())
                    .stockQuantity(event.getStockQuantity())
                    .active(event.isActive())
                    .build();

            syncUseCase.syncProduct(product);

            // --- ADD THIS LOG LINE ---
            log.info("✅ PRODUCT SUCCESSFULLY SAVED TO ELASTICSEARCH: {}", product.getName());
        }
    }
}