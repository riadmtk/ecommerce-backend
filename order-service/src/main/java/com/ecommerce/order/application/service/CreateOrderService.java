package com.ecommerce.order.application.service;

import com.ecommerce.order.domain.exception.StockUnavailableException;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.domain.port.in.CreateOrderUseCase;
import com.ecommerce.order.domain.port.out.CartServicePort;
import com.ecommerce.order.domain.port.out.OrderEventPublisherPort;
import com.ecommerce.order.domain.port.out.OrderRepositoryPort;
import com.ecommerce.order.domain.port.out.ProductServiceClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final ProductServiceClientPort productServiceClient;
    private final OrderEventPublisherPort eventPublisher;
    private final CartServicePort cartServicePort;

    @Override
    @Transactional
    public Order createOrder(CreateOrderCommand command) {
        // Récupérer les articles depuis le panier (cart-service)
        List<OrderItem> items = cartServicePort.getCartItems(command.userId(), command.token());
        if (items.isEmpty()) {
            throw new IllegalStateException("Le panier est vide");
        }

        // Vérifier et réserver le stock
        try {
            productServiceClient.validateAndReserveStock(items);
        } catch (Exception e) {
            throw new StockUnavailableException("Stock validation failed");
        }

        // Créer la commande
        Order order = Order.create(command.userId(), items, command.shippingAddress());
        Order savedOrder = orderRepository.save(order);

        // 🎯 LE CORRECTIF EST ICI : On passe command.email() en deuxième paramètre !
        eventPublisher.publishOrderCreated(savedOrder, command.email());

        return savedOrder;
    }
}