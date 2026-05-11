package com.ecommerce.product.infrastructure.adapter.out.messaging;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.port.out.ProductEventPublisherPort;
import com.ecommerce.product.infrastructure.adapter.out.messaging.dto.ProductEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventPublisher implements ProductEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.product-events}")
    private String productEventsTopic;

    @Override
    public void publishProductDeleted(UUID productId) {
        // For delete, we just need the ID and the event type
        ProductEventMessage message = ProductEventMessage.builder()
                .eventType("DELETED")
                .id(productId)
                .build();

        kafkaTemplate.send(productEventsTopic, productId.toString(), message);
        log.info("ProductDeleted event sent for product {}", productId);
    }

    @Override
    public void publishProductCreated(Product product) {
        // Use the helper method we built earlier!
        ProductEventMessage message = ProductEventMessage.fromProduct("ProductCreated", product);

        kafkaTemplate.send(productEventsTopic, product.getId().toString(), message);
        log.info("ProductCreated event sent for product {}", product.getId());
    }

    @Override
    public void publishProductUpdated(Product product) {
        // Use the helper method we built earlier!
        ProductEventMessage message = ProductEventMessage.fromProduct("ProductUpdated", product);

        kafkaTemplate.send(productEventsTopic, product.getId().toString(), message);
        log.info("ProductUpdated event sent for product {}", product.getId());
    }
}