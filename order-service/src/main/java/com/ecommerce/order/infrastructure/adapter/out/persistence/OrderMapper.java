package com.ecommerce.order.infrastructure.adapter.out.persistence;

import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.infrastructure.adapter.out.persistence.entity.OrderEntity;
import com.ecommerce.order.infrastructure.adapter.out.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public Order toDomain(OrderEntity entity) {
        return Order.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .items(entity.getItems().stream().map(this::toDomainItem).collect(Collectors.toList()))
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .shippingAddress(entity.getShippingAddress())
                .paymentId(entity.getPaymentId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private OrderItem toDomainItem(OrderItemEntity entity) {
        return OrderItem.builder()
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .totalPrice(entity.getTotalPrice())
                .build();
    }

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = OrderEntity.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .paymentId(order.getPaymentId())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
        if (order.getItems() != null) {
            // Vider la collection existante pour éviter les orphelins
            entity.getItems().clear();
            // Ajouter chaque item en passant l'entité parent pour la relation bidirectionnelle
            order.getItems().forEach(item ->
                    entity.getItems().add(toEntityItem(item, entity))
            );
        }
        return entity;
    }

    private OrderItemEntity toEntityItem(OrderItem item, OrderEntity order) {
        return OrderItemEntity.builder()
                .order(order)
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}