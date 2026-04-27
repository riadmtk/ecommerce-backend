package com.ecommerce.cart.domain.port.in;
import com.ecommerce.cart.domain.model.Cart;
import java.util.UUID;

public interface GetCartUseCase {
    Cart getCartByUserId(UUID userId);
}