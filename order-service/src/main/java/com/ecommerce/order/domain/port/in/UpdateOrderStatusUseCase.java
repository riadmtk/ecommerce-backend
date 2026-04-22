package com.ecommerce.order.domain.port.in;

import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderStatus;
import java.util.UUID;

public interface UpdateOrderStatusUseCase {
    Order updateStatus(UUID orderId, OrderStatus newStatus);
}