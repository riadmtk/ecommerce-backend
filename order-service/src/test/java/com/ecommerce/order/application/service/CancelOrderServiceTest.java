package com.ecommerce.order.application.service;

import com.ecommerce.order.domain.exception.OrderNotFoundException;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.domain.model.OrderStatus;
import com.ecommerce.order.domain.port.out.OrderEventPublisherPort;
import com.ecommerce.order.domain.port.out.OrderRepositoryPort;
import com.ecommerce.order.domain.port.out.ProductServiceClientPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CancelOrderService - Tests Application")
class CancelOrderServiceTest {

    @Mock private OrderRepositoryPort orderRepository;
    @Mock private ProductServiceClientPort productServiceClient;
    @Mock private OrderEventPublisherPort eventPublisher;
    @InjectMocks private CancelOrderService cancelOrderService;

    private UUID orderId;
    private Order order;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        order = Order.builder()
                .id(orderId)
                .userId(UUID.randomUUID())
                .items(List.of(OrderItem.builder().productId(UUID.randomUUID()).quantity(1).build()))
                .totalAmount(new BigDecimal("100.00"))
                .status(OrderStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Doit annuler une commande avec succès")
    void shouldCancelOrderSuccessfully() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = cancelOrderService.cancelOrder(orderId);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(productServiceClient).releaseStock(order.getItems());
        verify(eventPublisher).publishOrderCancelled(any(Order.class));
    }

    @Test
    @DisplayName("Doit lever OrderNotFoundException si commande introuvable")
    void shouldThrowWhenOrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cancelOrderService.cancelOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("Ne doit pas annuler si la commande est déjà expédiée (exception métier)")
    void shouldNotCancelIfAlreadyShipped() {
        Order shippedOrder = order.updateStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(shippedOrder));

        assertThatThrownBy(() -> cancelOrderService.cancelOrder(orderId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cancel order");
        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publishOrderCancelled(any());
    }
}