package com.ecommerce.order.infrastructure.adapter.in.web.dto;

import com.ecommerce.order.domain.model.OrderStatus;

public record UpdateStatusRequest(OrderStatus newStatus) {}