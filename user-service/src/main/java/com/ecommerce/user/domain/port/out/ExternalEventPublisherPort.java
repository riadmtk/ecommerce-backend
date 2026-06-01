package com.ecommerce.user.domain.port.out;

public interface ExternalEventPublisherPort {
    void publish(String eventType, Object data);
}