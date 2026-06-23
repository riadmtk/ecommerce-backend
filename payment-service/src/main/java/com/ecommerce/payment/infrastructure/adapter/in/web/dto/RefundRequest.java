package com.ecommerce.payment.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RefundRequest(
        @NotBlank(message = "Le motif du remboursement est obligatoire")
        String reason
) {}