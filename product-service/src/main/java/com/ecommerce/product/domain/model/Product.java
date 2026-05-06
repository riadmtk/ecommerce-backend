package com.ecommerce.product.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private UUID id;
    private String name;
    private String category;
    private String imageUrl;
    private String description;
    private BigDecimal price; // Changement effectué ici
    private int stockQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isAvailable() {
        return this.stockQuantity > 0;
    }

    @Builder.Default
    private boolean active = true;
}