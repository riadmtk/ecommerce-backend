package com.ecommerce.search.infrastructure.adapter.in.messaging;

import com.ecommerce.search.domain.model.Product;
import com.ecommerce.search.domain.port.in.SyncProductUseCase;
import com.ecommerce.search.infrastructure.adapter.in.messaging.dto.ProductEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductEventConsumer {

    private final SyncProductUseCase syncUseCase;

    public ProductEventConsumer(SyncProductUseCase syncUseCase) {
        this.syncUseCase = syncUseCase;
    }

    @KafkaListener(topics = "product.events", groupId = "search-service-group")
    public void consume(ProductEvent event) {

        log.info("🔔 KAFKA MESSAGE RECEIVED IN SEARCH SERVICE: {}", event);

        if (event.getId() == null) {
            log.error("❌ Received ProductEvent with null ID. Dropping message.");
            return;
        }

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
                    .categoryId(event.getCategoryId())     // ← Map ID
                    .categoryName(event.getCategoryName()) // ← Map Name
                    .imageUrls(event.getImageUrls())
                    .build();

            syncUseCase.syncProduct(product);

            log.info("✅ PRODUCT SUCCESSFULLY SAVED TO ELASTICSEARCH: {}", product.getName());
        }
    }
}