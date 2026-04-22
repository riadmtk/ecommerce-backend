package com.ecommerce.order.infrastructure.adapter.out.persistence.entity;

import com.ecommerce.order.domain.model.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class OrderEntity {
    @Id
    private UUID id;
    private UUID userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String shippingAddress;
    private String paymentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}