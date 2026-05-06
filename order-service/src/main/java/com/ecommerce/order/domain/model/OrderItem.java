package com.ecommerce.order.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class OrderItem {
    private UUID productId;
    @Builder.Default
    private String productName = "Produit inconnu";
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}