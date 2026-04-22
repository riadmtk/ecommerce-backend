package com.ecommerce.order.infrastructure.adapter.in.web;

import com.ecommerce.order.domain.exception.OrderNotFoundException;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.domain.model.OrderStatus;
import com.ecommerce.order.domain.port.in.CancelOrderUseCase;
import com.ecommerce.order.domain.port.in.CreateOrderUseCase;
import com.ecommerce.order.domain.port.in.GetOrderUseCase;
import com.ecommerce.order.infrastructure.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class, excludeAutoConfiguration = KafkaAutoConfiguration.class)
@Import(SecurityConfig.class)
@DisplayName("OrderController - Tests Infrastructure")
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CreateOrderUseCase createOrderUseCase;
    @MockBean private GetOrderUseCase getOrderUseCase;
    @MockBean private CancelOrderUseCase cancelOrderUseCase;
    @MockBean private JwtDecoder jwtDecoder; // requis pour la sécurité OAuth2

    private UUID userId;
    private UUID orderId;
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        mockOrder = Order.builder()
                .id(orderId)
                .userId(userId)
                .items(List.of(OrderItem.builder()
                        .productId(UUID.randomUUID())
                        .productName("Laptop")
                        .quantity(1)
                        .unitPrice(new BigDecimal("999.99"))
                        .totalPrice(new BigDecimal("999.99"))
                        .build()))
                .totalAmount(new BigDecimal("999.99"))
                .status(OrderStatus.PENDING)
                .shippingAddress("123 rue de Test")
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/orders - Doit créer une commande et retourner 201")
    void shouldCreateOrderAndReturn201() throws Exception {
        when(createOrderUseCase.createOrder(any())).thenReturn(mockOrder);

        String requestBody = """
                {
                    "items": [
                        {
                            "productId": "%s",
                            "productName": "Laptop",
                            "quantity": 1,
                            "unitPrice": 999.99,
                            "totalPrice": 999.99
                        }
                    ],
                    "shippingAddress": "123 rue de Test"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/orders")
                        .with(jwt().jwt(j -> j.claim("userId", userId.toString())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.totalAmount").value(999.99))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/orders - Doit retourner 401 sans JWT")
    void shouldReturn401WithoutJwt() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/orders/{id} - Doit retourner une commande par ID")
    void shouldReturnOrderById() throws Exception {
        when(getOrderUseCase.getById(orderId)).thenReturn(mockOrder);
        mockMvc.perform(get("/api/orders/{id}", orderId)
                        .with(jwt().jwt(j -> j.claim("userId", userId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/orders/{id} - Doit retourner 404 si commande introuvable")
    void shouldReturn404WhenOrderNotFound() throws Exception {
        when(getOrderUseCase.getById(orderId)).thenThrow(new OrderNotFoundException(orderId));
        mockMvc.perform(get("/api/orders/{id}", orderId)
                        .with(jwt().jwt(j -> j.claim("userId", userId.toString()))))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/orders/me - Doit retourner les commandes de l'utilisateur connecté")
    void shouldReturnMyOrders() throws Exception {
        when(getOrderUseCase.getByUserId(userId)).thenReturn(List.of(mockOrder));
        mockMvc.perform(get("/api/orders/me")
                        .with(jwt().jwt(j -> j.claim("userId", userId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(orderId.toString()));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/orders/{id}/cancel - Doit annuler une commande")
    void shouldCancelOrder() throws Exception {
        Order cancelledOrder = mockOrder.updateStatus(OrderStatus.CANCELLED);
        when(cancelOrderUseCase.cancelOrder(orderId)).thenReturn(cancelledOrder);
        mockMvc.perform(post("/api/orders/{id}/cancel", orderId)
                        .with(jwt().jwt(j -> j.claim("userId", userId.toString())))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}