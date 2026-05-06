package com.ecommerce.order.application.service;

import com.ecommerce.order.domain.exception.StockUnavailableException;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.domain.model.OrderStatus;
import com.ecommerce.order.domain.port.in.CreateOrderUseCase;
import com.ecommerce.order.domain.port.out.CartServicePort;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateOrderService - Tests Application")
class CreateOrderServiceTest {

    @Mock private OrderRepositoryPort orderRepository;
    @Mock private ProductServiceClientPort productServiceClient;
    @Mock private OrderEventPublisherPort eventPublisher;
    @Mock private CartServicePort cartServicePort;
    @InjectMocks private CreateOrderService createOrderService;

    private UUID userId;
    private String token;
    private List<OrderItem> cartItems;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        token = "dummy-jwt-token";
        cartItems = List.of(
                OrderItem.builder()
                        .productId(UUID.randomUUID())
                        .productName("Laptop")
                        .quantity(1)
                        .unitPrice(new BigDecimal("999.99"))
                        .totalPrice(new BigDecimal("999.99"))
                        .build()
        );
    }

    @Test
    @DisplayName("Doit créer une commande avec succès à partir du panier")
    void shouldCreateOrderSuccessfully() {
        // Given
        when(cartServicePort.getCartItems(userId, token)).thenReturn(cartItems);
        doNothing().when(productServiceClient).validateAndReserveStock(cartItems);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        CreateOrderUseCase.CreateOrderCommand command =
                new CreateOrderUseCase.CreateOrderCommand(userId, "123 rue de la Paix", token);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("999.99"));
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.getShippingAddress()).isEqualTo("123 rue de la Paix");

        verify(productServiceClient).validateAndReserveStock(cartItems);
        verify(cartServicePort).clearCart(userId, token);
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderCreated(any(Order.class));
    }

    @Test
    @DisplayName("Doit lever StockUnavailableException si la validation du stock échoue")
    void shouldThrowWhenStockValidationFails() {
        when(cartServicePort.getCartItems(userId, token)).thenReturn(cartItems);
        doThrow(new RuntimeException("Stock error")).when(productServiceClient).validateAndReserveStock(cartItems);

        CreateOrderUseCase.CreateOrderCommand command =
                new CreateOrderUseCase.CreateOrderCommand(userId, "adresse", token);

        assertThatThrownBy(() -> createOrderService.createOrder(command))
                .isInstanceOf(StockUnavailableException.class);

        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publishOrderCreated(any());
        verify(cartServicePort, never()).clearCart(any(UUID.class), any(String.class)); // panier conservé
    }

    @Test
    @DisplayName("Doit lever IllegalStateException si le panier est vide")
    void shouldThrowWhenCartIsEmpty() {
        when(cartServicePort.getCartItems(userId, token)).thenReturn(Collections.emptyList());

        CreateOrderUseCase.CreateOrderCommand command =
                new CreateOrderUseCase.CreateOrderCommand(userId, "adresse", token);

        assertThatThrownBy(() -> createOrderService.createOrder(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("panier est vide");
    }
}