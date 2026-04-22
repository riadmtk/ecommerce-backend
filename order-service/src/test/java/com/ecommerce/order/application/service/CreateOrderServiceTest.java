package com.ecommerce.order.application.service;

import com.ecommerce.order.domain.exception.StockUnavailableException;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.domain.model.OrderStatus;
import com.ecommerce.order.domain.port.in.CreateOrderUseCase;
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
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateOrderService - Tests Application")
class CreateOrderServiceTest {

    @Mock private OrderRepositoryPort orderRepository;
    @Mock private ProductServiceClientPort productServiceClient;
    @Mock private OrderEventPublisherPort eventPublisher;
    @InjectMocks private CreateOrderService createOrderService;

    private UUID userId;
    private List<OrderItem> items;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        items = List.of(
                OrderItem.builder()
                        .productId(UUID.randomUUID())
                        .productName("Test Product")
                        .quantity(2)
                        .unitPrice(new BigDecimal("10.00"))
                        .totalPrice(new BigDecimal("20.00"))
                        .build()
        );
    }

    @Test
    @DisplayName("Doit créer une commande avec succès")
    void shouldCreateOrderSuccessfully() {
        doNothing().when(productServiceClient).validateAndReserveStock(items);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        CreateOrderUseCase.CreateOrderCommand command =
                new CreateOrderUseCase.CreateOrderCommand(userId, items, "123 rue Test");

        Order result = createOrderService.createOrder(command);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(productServiceClient).validateAndReserveStock(items);
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderCreated(any(Order.class));
    }

    @Test
    @DisplayName("Doit lever StockUnavailableException si validation stock échoue")
    void shouldThrowWhenStockValidationFails() {
        doThrow(new RuntimeException("Stock error")).when(productServiceClient).validateAndReserveStock(items);
        CreateOrderUseCase.CreateOrderCommand command =
                new CreateOrderUseCase.CreateOrderCommand(userId, items, "adresse");
        assertThatThrownBy(() -> createOrderService.createOrder(command))
                .isInstanceOf(StockUnavailableException.class);
        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publishOrderCreated(any());
    }
}