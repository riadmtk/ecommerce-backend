package com.ecommerce.order.infrastructure.adapter.in.web;

import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.port.in.CancelOrderUseCase;
import com.ecommerce.order.domain.port.in.CreateOrderUseCase;
import com.ecommerce.order.domain.port.in.GetOrderUseCase;
import com.ecommerce.order.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import com.ecommerce.order.infrastructure.adapter.in.web.dto.OrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateOrderRequest request) {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));
        Order order = createOrderUseCase.createOrder(
                new CreateOrderUseCase.CreateOrderCommand(userId, request.items(), request.shippingAddress())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        Order order = getOrderUseCase.getById(id);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("/me")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));
        List<Order> orders = getOrderUseCase.getByUserId(userId);
        return ResponseEntity.ok(orders.stream().map(OrderResponse::from).toList());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID id) {
        Order order = cancelOrderUseCase.cancelOrder(id);
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}