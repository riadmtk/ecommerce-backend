package com.ecommerce.order.domain.port.in;

import com.ecommerce.order.domain.model.Order;
import java.util.UUID;

public interface CancelOrderUseCase {
    Order cancelOrder(UUID orderId);
}