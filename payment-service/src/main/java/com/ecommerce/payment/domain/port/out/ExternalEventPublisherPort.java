package com.ecommerce.payment.domain.port.out;

public interface ExternalEventPublisherPort {
    void publishEvent(String eventType, Object data);
}