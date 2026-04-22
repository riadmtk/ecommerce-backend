package com.ecommerce.order.application.service;

import com.ecommerce.order.domain.exception.OrderNotFoundException;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderStatus;
import com.ecommerce.order.domain.port.out.OrderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetOrderService - Tests Application")
class GetOrderServiceTest {

    @Mock private OrderRepositoryPort orderRepository;
    @InjectMocks private GetOrderService getOrderService;

    private UUID orderId;
    private UUID userId;
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        userId = UUID.randomUUID();
        mockOrder = Order.builder()
                .id(orderId)
                .userId(userId)
                .items(List.of())
                .totalAmount(BigDecimal.ZERO)
                .status(OrderStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Doit retourner une commande par son ID")
    void shouldReturnOrderById() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        Order result = getOrderService.getById(orderId);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderId);
    }

    @Test
    @DisplayName("Doit lever OrderNotFoundException si ID introuvable")
    void shouldThrowWhenOrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> getOrderService.getById(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("Doit retourner les commandes d'un utilisateur")
    void shouldReturnOrdersByUserId() {
        when(orderRepository.findByUserId(userId)).thenReturn(List.of(mockOrder));
        List<Order> orders = getOrderService.getByUserId(userId);
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getUserId()).isEqualTo(userId);
    }
}