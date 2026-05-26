package com.ecommerce.payment.infrastructure.adapter.in.web.dto;

import com.ecommerce.payment.domain.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record InitiatePaymentRequest(

        @NotNull(message = "orderId obligatoire")
        UUID orderId,

        @NotNull(message = "amount obligatoire")
        @Positive(message = "amount doit être positif")
        BigDecimal amount,

        @NotNull(message = "currency obligatoire")
        String currency,

        @NotNull(message = "paymentMethod obligatoire")
        PaymentMethod paymentMethod
) {}