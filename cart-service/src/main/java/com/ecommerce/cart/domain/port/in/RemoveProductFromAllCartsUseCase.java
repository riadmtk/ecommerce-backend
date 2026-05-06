package com.ecommerce.cart.domain.port.in;

import java.util.UUID;

public interface RemoveProductFromAllCartsUseCase {
    void removeProductFromAllCarts(UUID productId);
}