package com.ecommerce.cart.domain.port.in;
import com.ecommerce.cart.domain.model.Cart;
import java.util.UUID;

public interface RemoveItemFromCartUseCase {
    Cart removeItem(UUID userId, UUID productId);
}