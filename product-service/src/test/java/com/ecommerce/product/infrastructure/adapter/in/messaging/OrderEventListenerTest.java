package com.ecommerce.product.infrastructure.adapter.in.messaging;

import com.ecommerce.product.domain.port.in.UpdateProductStockUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventListenerTest {

    @Mock
    private UpdateProductStockUseCase updateProductStockUseCase;

    // On utilise un vrai ObjectMapper plutôt que de le mocker, c'est beaucoup plus simple pour tester le parsing JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    private OrderEventListener orderEventListener;

    @BeforeEach
    void setUp() {
        // On instancie le Listener avec le mock du UseCase et le vrai ObjectMapper
        orderEventListener = new OrderEventListener(updateProductStockUseCase, objectMapper);
    }

    @Test
    void shouldCallDecreaseStockWhenOrderCreatedEventReceived() {
        // Arrange
        UUID productId = UUID.randomUUID();

        // On simule exactement la structure JSON que le Order Service va envoyer
        String jsonMessage = """
                {
                    "eventType": "OrderCreated",
                    "orderId": "12345",
                    "items": [
                        {
                            "productId": "%s",
                            "quantity": 2
                        }
                    ]
                }
                """.formatted(productId.toString());

        // Act
        orderEventListener.handleOrderCreatedEvent(jsonMessage);

        // Assert
        // On vérifie que le use case a bien été appelé avec l'ID du produit et la bonne quantité (2)
        verify(updateProductStockUseCase, times(1)).decreaseStock(eq(productId), eq(2));
    }

    @Test
    void shouldHandleExceptionsGracefully() {
        // Arrange
        String invalidJsonMessage = "Ceci n'est pas un JSON valide";

        // Act
        // La méthode doit catcher l'exception en interne grâce au try-catch, le test ne doit donc pas crasher
        orderEventListener.handleOrderCreatedEvent(invalidJsonMessage);

        // Assert
        // On vérifie que si le JSON est mauvais, le UseCase n'est jamais appelé
        verify(updateProductStockUseCase, never()).decreaseStock(any(UUID.class), anyInt());
    }
}