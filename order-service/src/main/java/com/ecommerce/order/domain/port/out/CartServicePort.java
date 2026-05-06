package com.ecommerce.order.domain.port.out;

import com.ecommerce.order.domain.model.OrderItem;
import java.util.List;
import java.util.UUID;

public interface CartServicePort {
    List<OrderItem> getCartItems(UUID userId, String token);
    void clearCart(UUID userId, String token);
}