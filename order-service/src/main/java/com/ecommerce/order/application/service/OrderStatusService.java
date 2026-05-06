package com.ecommerce.order.application.service;

import com.ecommerce.order.domain.exception.OrderNotFoundException;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderStatus;
import com.ecommerce.order.domain.port.in.UpdateOrderStatusUseCase;
import com.ecommerce.order.domain.port.out.OrderEventPublisherPort;
import com.ecommerce.order.domain.port.out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderStatusService implements UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderEventPublisherPort eventPublisher;

    @Override
    public Order updateStatus(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        Order updatedOrder = order.updateStatus(newStatus);
        updatedOrder = orderRepository.save(updatedOrder);

        // Publier un événement si le nouveau statut est REFUNDED
        if (newStatus == OrderStatus.REFUNDED) {
            eventPublisher.publishOrderRefunded(updatedOrder);
        }
        return updatedOrder;
    }
}