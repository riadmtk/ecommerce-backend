package com.ecommerce.payment.infrastructure.adapter.in.web.dto;

import com.ecommerce.payment.domain.model.Payment;
import com.ecommerce.payment.domain.model.PaymentMethod;
import com.ecommerce.payment.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCreatedResponse(
        UUID id,
        UUID orderId,
        BigDecimal amount,
        String currency,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        String transactionId,
        String clientSecret  // uniquement dans la réponse de création
) {
    public static PaymentCreatedResponse from(Payment payment) {
        return new PaymentCreatedResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getClientSecret()
        );
    }
}