package com.ecommerce.order.infrastructure.adapter.out.messaging;

import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.port.out.OrderEventPublisherPort;
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
public class OrderEventPublisherAdapter implements OrderEventPublisherPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.order-events}")
    private String orderEventsTopic;

    @Override
    public void publishOrderCreated(Order order) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "OrderCreated");
        event.put("timestamp", Instant.now().toString());
        event.put("service", "order-service");
        event.put("version", "1.0");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("orderId", order.getId().toString());
        payload.put("userId", order.getUserId().toString());
        payload.put("totalAmount", order.getTotalAmount());
        payload.put("status", order.getStatus().name());
        // On peut aussi ajouter les items si nécessaire, mais le payload doit rester lisible

        event.put("payload", payload);
        kafkaTemplate.send(orderEventsTopic, order.getId().toString(), event);
        log.info("OrderCreated event sent for order {}", order.getId());
    }

    @Override
    public void publishOrderCancelled(Order order) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "OrderCancelled");
        event.put("timestamp", Instant.now().toString());
        event.put("service", "order-service");
        event.put("version", "1.0");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("orderId", order.getId().toString());
        payload.put("userId", order.getUserId().toString());

        event.put("payload", payload);
        kafkaTemplate.send(orderEventsTopic, order.getId().toString(), event);
        log.info("OrderCancelled event sent for order {}", order.getId());
    }
}