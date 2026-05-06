package com.ecommerce.cart.domain.port.in;

import com.ecommerce.cart.domain.model.Cart;
import java.util.UUID;

public interface UpdateCartItemQuantityUseCase {
    record UpdateCartItemQuantityCommand(UUID userId, UUID productId, int quantity) {}
    Cart updateItemQuantity(UpdateCartItemQuantityCommand command);
}