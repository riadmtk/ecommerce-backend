package com.ecommerce.order.application.service;

import com.ecommerce.order.domain.exception.OrderNotFoundException;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.port.in.CancelOrderUseCase;
import com.ecommerce.order.domain.port.out.OrderEventPublisherPort;
import com.ecommerce.order.domain.port.out.OrderRepositoryPort;
import com.ecommerce.order.domain.port.out.ProductServiceClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelOrderService implements CancelOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final ProductServiceClientPort productServiceClient;
    private final OrderEventPublisherPort eventPublisher;

    @Override
    @Transactional
    public Order cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        Order cancelledOrder = order.cancel();
        orderRepository.save(cancelledOrder);

        // Libérer le stock réservé
        productServiceClient.releaseStock(order.getItems());

        // Publier l'événement d'annulation
        eventPublisher.publishOrderCancelled(cancelledOrder);

        return cancelledOrder;
    }
}