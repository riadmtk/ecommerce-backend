package com.ecommerce.order.domain.port.in;

import com.ecommerce.order.domain.model.Order;
import java.util.UUID;

public interface CreateOrderUseCase {
    record CreateOrderCommand(UUID userId, String shippingAddress, String token) {}
    Order createOrder(CreateOrderCommand command);
}