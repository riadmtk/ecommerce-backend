package com.ecommerce.product.infrastructure.adapter.out.messaging;

import com.ecommerce.product.domain.port.out.ProductEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "ProductDeleted");
        event.put("productId", productId.toString());

        kafkaTemplate.send(productEventsTopic, productId.toString(), event);
        log.info("ProductDeleted event sent for product {}", productId);
    }
}