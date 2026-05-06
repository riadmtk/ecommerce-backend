package com.ecommerce.order.domain.port.out;

import com.ecommerce.order.domain.model.Order;

public interface OrderEventPublisherPort {
    void publishOrderCreated(Order order);
    void publishOrderCancelled(Order order);
    void publishOrderRefunded(Order order);
}