package com.ecommerce.order.infrastructure.adapter.in.web.dto;

import com.ecommerce.order.domain.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull(message = "New status is required")
        OrderStatus newStatus
) {}