package com.ecommerce.order.infrastructure.adapter.out.messaging;

import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.port.out.OrderEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisherAdapter implements OrderEventPublisherPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.order-events}")
    private String orderEventsTopic;

    @Override
    public void publishOrderCreated(Order order) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "OrderCreated");
        event.put("orderId", order.getId().toString());
        event.put("userId", order.getUserId().toString());
        event.put("totalAmount", order.getTotalAmount());
        event.put("status", order.getStatus().name());
        kafkaTemplate.send(orderEventsTopic, order.getId().toString(), event);
        log.info("OrderCreated event sent for order {}", order.getId());
    }

    @Override
    public void publishOrderCancelled(Order order) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "OrderCancelled");
        event.put("orderId", order.getId().toString());
        event.put("userId", order.getUserId().toString());
        kafkaTemplate.send(orderEventsTopic, order.getId().toString(), event);
        log.info("OrderCancelled event sent for order {}", order.getId());
    }
}