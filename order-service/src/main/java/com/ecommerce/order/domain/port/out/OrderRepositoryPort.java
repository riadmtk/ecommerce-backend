package com.ecommerce.order.domain.port.out;

import com.ecommerce.order.domain.model.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findByUserId(UUID userId);
    List<Order> findAll();
}