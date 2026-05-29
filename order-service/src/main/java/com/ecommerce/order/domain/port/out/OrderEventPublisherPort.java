package com.ecommerce.order.domain.port.out;

import com.ecommerce.order.domain.model.Order;

public interface OrderEventPublisherPort {
    void publishOrderCreated(Order order, String userEmail);
    void publishOrderCancelled(Order order);
    void publishOrderRefunded(Order order);
    void publishOrderStatusUpdated(Order order, String previousStatus);
}