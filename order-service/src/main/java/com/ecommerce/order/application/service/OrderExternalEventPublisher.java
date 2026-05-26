package com.ecommerce.order.application.service;

import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.port.out.ExternalEventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "external.kafka.enabled", havingValue = "true")
@RequiredArgsConstructor
public class OrderExternalEventPublisher {

    private final ExternalEventPublisherPort externalPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderEvent(Order order) {
        String eventType = switch (order.getStatus()) {
            case PENDING -> "order_placed";
            case PAID -> "order_paid";
            case PROCESSING -> "order_processing";
            case SHIPPED -> "order_shipped";
            case DELIVERED -> "order_delivered";
            case CANCELLED -> "order_cancelled";
            case REFUNDED -> "order_refunded";
            default -> null;
        };

        if (eventType == null) return;

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("order_id", order.getId().toString());
        data.put("customer_id", order.getUserId().toString());
        data.put("total_amount", order.getTotalAmount());
        data.put("items_count", order.getItems().size());
        data.put("status", order.getStatus().name());

        externalPublisher.publish(eventType, data);
    }
}