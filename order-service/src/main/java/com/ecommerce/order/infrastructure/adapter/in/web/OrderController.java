package com.ecommerce.order.infrastructure.adapter.in.web;

import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderStatus;
import com.ecommerce.order.domain.port.in.*;
import com.ecommerce.order.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import com.ecommerce.order.infrastructure.adapter.in.web.dto.OrderResponse;
import com.ecommerce.order.infrastructure.adapter.in.web.dto.UpdateStatusRequest;
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
    private final GetAllOrdersUseCase getAllOrdersUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateOrderRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());  // ✅
        String token = jwt.getTokenValue();
        Order order = createOrderUseCase.createOrder(
                new CreateOrderUseCase.CreateOrderCommand(userId, request.shippingAddress(), token));
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        Order order = getOrderUseCase.getById(id);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("/me")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());  // ✅
        List<Order> orders = getOrderUseCase.getByUserId(userId);
        return ResponseEntity.ok(orders.stream().map(OrderResponse::from).toList());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID id) {
        Order order = cancelOrderUseCase.cancelOrder(id);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(getAllOrdersUseCase.getAllOrders().stream()
                .map(OrderResponse::from).toList());
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<OrderResponse> deliverOrder(@PathVariable UUID id) {
        Order order = getOrderUseCase.getById(id);
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new IllegalStateException("La commande doit être expédiée pour être livrée");
        }
        Order updated = updateOrderStatusUseCase.updateStatus(id, OrderStatus.DELIVERED);
        return ResponseEntity.ok(OrderResponse.from(updated));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdateStatusRequest request) {
        Order order = updateOrderStatusUseCase.updateStatus(id, request.newStatus());
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}