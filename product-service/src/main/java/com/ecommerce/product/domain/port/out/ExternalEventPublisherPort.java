package com.ecommerce.product.domain.port.out;

public interface ExternalEventPublisherPort {
    void publish(String eventType, Object data);
}
