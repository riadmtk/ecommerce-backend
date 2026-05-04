package com.ecommerce.order.infrastructure.adapter.in.web.dto;

import com.ecommerce.order.domain.model.OrderItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateOrderRequest(
       /* @NotEmpty(message = "Items cannot be empty")
        @Valid
        List<OrderItem> items,
        */
        @NotBlank(message = "Shipping address is required")
        String shippingAddress
) {}