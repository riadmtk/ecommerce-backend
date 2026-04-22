package com.ecommerce.order.domain.port.in;

import com.ecommerce.order.domain.model.Order;
import java.util.List;
import java.util.UUID;

public interface GetOrderUseCase {
    Order getById(UUID orderId);
    List<Order> getByUserId(UUID userId);
}