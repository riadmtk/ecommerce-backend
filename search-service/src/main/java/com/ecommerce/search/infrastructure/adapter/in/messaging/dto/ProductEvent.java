package com.ecommerce.search.infrastructure.adapter.in.messaging.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductEvent {
    private String eventType; // e.g., "CREATED", "UPDATED", "DELETED"
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private boolean active;
}