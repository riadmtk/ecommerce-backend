package com.ecommerce.order.domain.port.in;

import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderItem;
import java.util.List;
import java.util.UUID;

public interface CreateOrderUseCase {
    record CreateOrderCommand(UUID userId, List<OrderItem> items, String shippingAddress) {}
    Order createOrder(CreateOrderCommand command);
}