package com.ecommerce.cart.domain.port.in;
import java.util.UUID;

public interface ClearCartUseCase {
    void clearCart(UUID userId);
}