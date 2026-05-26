package com.ecommerce.order.domain.port.out;

public interface ExternalEventPublisherPort {
    void publish(String eventType, Object data);
}