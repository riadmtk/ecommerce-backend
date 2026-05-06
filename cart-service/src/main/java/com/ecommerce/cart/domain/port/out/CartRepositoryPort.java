package com.ecommerce.cart.domain.port.out;

import com.ecommerce.cart.domain.model.Cart;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepositoryPort {
    Optional<Cart> findByUserId(UUID userId);
    List<Cart> findCartsByProductId(UUID productId);
    Cart save(Cart cart);
}