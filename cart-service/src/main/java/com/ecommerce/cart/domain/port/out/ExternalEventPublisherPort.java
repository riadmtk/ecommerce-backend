package com.ecommerce.cart.domain.port.out;

public interface ExternalEventPublisherPort {
    void publish(String eventType, Object data);
}
