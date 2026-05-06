package com.ecommerce.order.application.service;

import com.ecommerce.order.domain.exception.OrderNotFoundException;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.port.in.GetOrderUseCase;
import com.ecommerce.order.domain.port.out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetOrderService implements GetOrderUseCase {

    private final OrderRepositoryPort orderRepository;

    @Override
    public Order getById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    public List<Order> getByUserId(UUID userId) {
        return orderRepository.findByUserId(userId);
    }
}