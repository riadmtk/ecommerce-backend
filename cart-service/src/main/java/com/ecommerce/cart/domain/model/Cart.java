package com.ecommerce.cart.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    private UUID id;
    private UUID userId;

    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}