package com.ecommerce.order.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

@DisplayName("Order - Tests Domaine")
class OrderTest {

    @Test
    @DisplayName("Doit créer une commande avec statut PENDING")
    void shouldCreateOrderWithPendingStatus() {
        UUID userId = UUID.randomUUID();
        List<OrderItem> items = List.of(
                OrderItem.builder()
                        .productId(UUID.randomUUID())
                        .productName("Laptop")
                        .quantity(1)
                        .unitPrice(new BigDecimal("999.99"))
                        .totalPrice(new BigDecimal("999.99"))
                        .build()
        );
        Order order = Order.create(userId, items, "123 rue de Paris, 75000 Paris");

        assertThat(order).isNotNull();
        assertThat(order.getId()).isNotNull();
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("999.99"));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getShippingAddress()).isEqualTo("123 rue de Paris, 75000 Paris");
        assertThat(order.getCreatedAt()).isNotNull();
        assertThat(order.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Doit calculer correctement le total de la commande")
    void shouldCalculateTotalAmountCorrectly() {
        List<OrderItem> items = List.of(
                OrderItem.builder().productId(UUID.randomUUID()).productName("A").quantity(2).unitPrice(new BigDecimal("10.00")).totalPrice(new BigDecimal("20.00")).build(),
                OrderItem.builder().productId(UUID.randomUUID()).productName("B").quantity(1).unitPrice(new BigDecimal("5.50")).totalPrice(new BigDecimal("5.50")).build()
        );
        Order order = Order.create(UUID.randomUUID(), items, "adresse");
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("25.50"));
    }

    @Test
    @DisplayName("Doit mettre à jour le statut de la commande")
    void shouldUpdateOrderStatus() {
        Order order = Order.create(UUID.randomUUID(), List.of(), "adresse");
        Order updated = order.updateStatus(OrderStatus.PAID);
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(order.getCreatedAt());
    }

    @Test
    @DisplayName("Doit annuler une commande si elle n'est ni expédiée ni livrée")
    void shouldCancelOrderIfNotShippedOrDelivered() {
        Order order = Order.create(UUID.randomUUID(), List.of(), "adresse");
        Order cancelled = order.cancel();
        assertThat(cancelled.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("Ne doit pas permettre l'annulation si la commande est expédiée")
    void shouldNotAllowCancelIfShipped() {
        Order order = Order.create(UUID.randomUUID(), List.of(), "adresse");
        Order shipped = order.updateStatus(OrderStatus.SHIPPED);
        assertThatThrownBy(shipped::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cancel order in status SHIPPED");
    }

    @Test
    @DisplayName("Ne doit pas permettre l'annulation si la commande est livrée")
    void shouldNotAllowCancelIfDelivered() {
        Order order = Order.create(UUID.randomUUID(), List.of(), "adresse");
        Order delivered = order.updateStatus(OrderStatus.DELIVERED);
        assertThatThrownBy(delivered::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cancel order in status DELIVERED");
    }
}