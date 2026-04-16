package com.ecommerce.product.infrastructure.adapter.in.messaging;

import com.ecommerce.product.domain.port.in.UpdateProductStockUseCase;
import com.ecommerce.product.infrastructure.adapter.in.messaging.dto.OrderCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventListenerTest {

    @Mock
    private UpdateProductStockUseCase updateProductStockUseCase;

    @InjectMocks
    private OrderEventListener orderEventListener;

    @Test
    void shouldCallDecreaseStockWhenOrderCreatedEventReceived() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        int quantity = 3;

        // On simule l'objet qui serait normalement désérialisé par Spring Kafka
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, productId, quantity);

        // --- ACT ---
        // On appelle directement la méthode du listener
        orderEventListener.handleOrderCreatedEvent(event);

        // --- ASSERT ---
        // On vérifie que le listener a bien transmis l'ordre au Domaine
        verify(updateProductStockUseCase, times(1)).decreaseStock(productId, quantity);
    }

    @Test
    void shouldHandleExceptionsGracefully() {
        // --- ARRANGE ---
        OrderCreatedEvent event = new OrderCreatedEvent(UUID.randomUUID(), UUID.randomUUID(), 1);

        // On simule une erreur dans le domaine (ex: produit introuvable ou stock insuffisant)
        doThrow(new RuntimeException("Domain Error"))
                .when(updateProductStockUseCase).decreaseStock(any(), anyInt());

        // --- ACT ---
        // L'appel ne doit pas faire planter le thread Kafka (grâce au try-catch dans le listener)
        orderEventListener.handleOrderCreatedEvent(event);

        // --- ASSERT ---
        // On vérifie simplement que l'appel a été tenté
        verify(updateProductStockUseCase, times(1)).decreaseStock(any(), anyInt());
    }
}