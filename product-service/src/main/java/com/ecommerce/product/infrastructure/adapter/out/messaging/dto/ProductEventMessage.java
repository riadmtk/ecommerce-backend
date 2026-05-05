package com.ecommerce.product.infrastructure.adapter.out.messaging.dto;

import com.ecommerce.product.domain.model.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ProductEventMessage {
    private String eventType;
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private boolean active;

    // Helper method to map your Domain Product to this Event DTO
    public static ProductEventMessage fromProduct(String eventType, Product product) {
        return ProductEventMessage.builder()
                .eventType(eventType)
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .active(product.isActive())
                .build();
    }
}