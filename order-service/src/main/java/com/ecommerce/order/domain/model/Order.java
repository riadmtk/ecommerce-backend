package com.ecommerce.order.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class Order {
    private UUID id;
    private UUID userId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private String paymentId;
    private String refundReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order updateStatus(OrderStatus newStatus) {
        return Order.builder()
                .id(this.id)
                .userId(this.userId)
                .items(this.items)
                .totalAmount(this.totalAmount)
                .status(newStatus)
                .shippingAddress(this.shippingAddress)
                .paymentId(this.paymentId)
                .refundReason(this.refundReason)   // ← ajout
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Order cancel() {
        if (this.status == OrderStatus.SHIPPED || this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel order in status " + this.status);
        }
        return updateStatus(OrderStatus.CANCELLED);
    }

    public static Order create(UUID userId, List<OrderItem> items, String shippingAddress) {
        BigDecimal total = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Order.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .items(items)
                .totalAmount(total)
                .status(OrderStatus.PENDING)
                .shippingAddress(shippingAddress)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}