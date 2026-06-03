package com.ecommerce.search.domain.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private UUID id;
    private String name;
    private String description;

    private UUID categoryId;
    private String categoryName;

    private BigDecimal price;
    private List<String> imageUrls;
    private int stockQuantity;
    private boolean active;
}