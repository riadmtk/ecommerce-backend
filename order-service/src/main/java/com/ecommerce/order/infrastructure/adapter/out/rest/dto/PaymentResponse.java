package com.ecommerce.order.infrastructure.adapter.out.rest.dto;

import java.util.UUID;

public record PaymentResponse(UUID id, UUID orderId, String status) {}
