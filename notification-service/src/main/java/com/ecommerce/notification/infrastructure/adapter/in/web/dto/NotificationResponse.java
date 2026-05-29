package com.ecommerce.notification.infrastructure.adapter.in.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String referenceId,
        String recipient,
        String status,
        String errorMessage,
        LocalDateTime createdAt
) {}