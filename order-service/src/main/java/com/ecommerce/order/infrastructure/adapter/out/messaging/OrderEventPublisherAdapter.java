package com.ecommerce.order.infrastructure.adapter.out.messaging;

import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.port.out.OrderEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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
        // ➕ Ajouter les articles pour la mise à jour du stock
        List<Map<String, Object>> items = order.getItems().stream().map(item -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("productId", item.getProductId().toString());
            map.put("quantity", item.getQuantity());
            return map;
        }).collect(Collectors.toList());
        payload.put("items", items);

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

        // ➕ Ajouter les articles pour la mise à jour du stock
        List<Map<String, Object>> items = order.getItems().stream().map(item -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("productId", item.getProductId().toString());
            map.put("quantity", item.getQuantity());
            return map;
        }).collect(Collectors.toList());
        payload.put("items", items);

        event.put("payload", payload);
        kafkaTemplate.send(orderEventsTopic, order.getId().toString(), event);
        log.info("OrderCancelled event sent for order {}", order.getId());
    }

    @Override
    public void publishOrderRefunded(Order order) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "OrderRefunded");
        event.put("timestamp", Instant.now().toString());
        event.put("service", "order-service");
        event.put("version", "1.0");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("orderId", order.getId().toString());
        payload.put("userId", order.getUserId().toString());
        // items nécessaires pour ré-augmenter le stock
        List<Map<String, Object>> items = order.getItems().stream().map(item -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("productId", item.getProductId().toString());
            map.put("quantity", item.getQuantity());
            return map;
        }).collect(Collectors.toList());
        payload.put("items", items);

        event.put("payload", payload);
        kafkaTemplate.send(orderEventsTopic, order.getId().toString(), event);
        log.info("OrderRefunded event sent for order {}", order.getId());
    }

    @Override
    public void publishOrderStatusUpdated(Order order, String previousStatus) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "OrderStatusUpdated");
        event.put("timestamp", Instant.now().toString());
        event.put("service", "order-service");
        event.put("version", "1.0");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("orderId", order.getId().toString());
        payload.put("previousStatus", previousStatus);
        payload.put("newStatus", order.getStatus().name());

        event.put("payload", payload);
        kafkaTemplate.send(orderEventsTopic, order.getId().toString(), event);
        log.info("OrderStatusUpdated event sent for order {}", order.getId());
    }
}