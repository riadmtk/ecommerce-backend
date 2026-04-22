package com.ecommerce.order.application.service;

import com.ecommerce.order.domain.exception.StockUnavailableException;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.port.in.CreateOrderUseCase;
import com.ecommerce.order.domain.port.out.OrderEventPublisherPort;
import com.ecommerce.order.domain.port.out.OrderRepositoryPort;
import com.ecommerce.order.domain.port.out.ProductServiceClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final ProductServiceClientPort productServiceClient;
    private final OrderEventPublisherPort eventPublisher;

    @Override
    @Transactional
    public Order createOrder(CreateOrderCommand command) {
        // Vérifier et réserver le stock
        try {
            productServiceClient.validateAndReserveStock(command.items());
        } catch (Exception e) {
            throw new StockUnavailableException("Stock validation failed");
        }

        Order order = Order.create(command.userId(), command.items(), command.shippingAddress());
        Order savedOrder = orderRepository.save(order);

        // Publier l'événement de création de commande
        eventPublisher.publishOrderCreated(savedOrder);

        return savedOrder;
    }
}