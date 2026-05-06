package com.ecommerce.product.infrastructure.adapter.out.messaging;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.port.out.ProductEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "ProductDeleted");
        event.put("timestamp", Instant.now().toString());
        event.put("service", "product-service");
        event.put("version", "1.0");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("productId", productId.toString());

        event.put("payload", payload);
        kafkaTemplate.send(productEventsTopic, productId.toString(), event);
        log.info("ProductDeleted event sent for product {}", productId);
    }

    @Override
    public void publishProductCreated(Product product) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "ProductCreated");
        event.put("timestamp", Instant.now().toString());
        event.put("service", "product-service");
        event.put("version", "1.0");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("productId", product.getId().toString());
        payload.put("name", product.getName());
        payload.put("price", product.getPrice());
        payload.put("stockQuantity", product.getStockQuantity());

        event.put("payload", payload);
        kafkaTemplate.send(productEventsTopic, product.getId().toString(), event);
        log.info("ProductCreated event sent for product {}", product.getId());
    }

    @Override
    public void publishProductUpdated(Product product) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "ProductUpdated");
        event.put("timestamp", Instant.now().toString());
        event.put("service", "product-service");
        event.put("version", "1.0");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("productId", product.getId().toString());
        payload.put("name", product.getName());
        payload.put("price", product.getPrice());
        payload.put("stockQuantity", product.getStockQuantity());

        event.put("payload", payload);
        kafkaTemplate.send(productEventsTopic, product.getId().toString(), event);
        log.info("ProductUpdated event sent for product {}", product.getId());
    }
}