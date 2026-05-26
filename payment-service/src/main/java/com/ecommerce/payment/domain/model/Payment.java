package com.ecommerce.payment.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    private UUID id;
    private UUID orderId;
    private UUID userId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionId;
    private String clientSecret;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Payment create(UUID orderId, UUID userId,
                                 BigDecimal amount, String currency,
                                 PaymentMethod method) {
        return Payment.builder()
                //.id(UUID.randomUUID())
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .currency(currency)
                .paymentMethod(method)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Payment succeed(String transactionId) {
        return Payment.builder()
                .id(this.id)
                .orderId(this.orderId)
                .userId(this.userId)
                .amount(this.amount)
                .currency(this.currency)
                .paymentMethod(this.paymentMethod)
                .status(PaymentStatus.SUCCESS)
                .transactionId(transactionId)
                .clientSecret(this.clientSecret)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Payment fail(String reason) {
        return Payment.builder()
                .id(this.id)
                .orderId(this.orderId)
                .userId(this.userId)
                .amount(this.amount)
                .currency(this.currency)
                .paymentMethod(this.paymentMethod)
                .status(PaymentStatus.FAILED)
                .transactionId(this.transactionId)
                .clientSecret(this.clientSecret)
                .failureReason(reason)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Payment refund() {
        return Payment.builder()
                .id(this.id)
                .orderId(this.orderId)
                .userId(this.userId)
                .amount(this.amount)
                .currency(this.currency)
                .paymentMethod(this.paymentMethod)
                .status(PaymentStatus.REFUNDED)
                .transactionId(this.transactionId)
                .clientSecret(this.clientSecret)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Payment withClientSecret(String clientSecret) {
        return Payment.builder()
                .id(this.id)
                .orderId(this.orderId)
                .userId(this.userId)
                .amount(this.amount)
                .currency(this.currency)
                .paymentMethod(this.paymentMethod)
                .status(this.status)
                .transactionId(this.transactionId)
                .clientSecret(clientSecret)
                .failureReason(this.failureReason)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    public Payment withStatus(PaymentStatus status) {
        return Payment.builder()
                .id(this.id)
                .orderId(this.orderId)
                .userId(this.userId)
                .amount(this.amount)
                .currency(this.currency)
                .paymentMethod(this.paymentMethod)
                .status(status)
                .transactionId(this.transactionId)
                .clientSecret(this.clientSecret)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Payment withTransactionId(String transactionId) {
        return Payment.builder()
                .id(this.id)
                .orderId(this.orderId)
                .userId(this.userId)
                .amount(this.amount)
                .currency(this.currency)
                .paymentMethod(this.paymentMethod)
                .status(this.status)
                .transactionId(transactionId)
                .clientSecret(this.clientSecret)
                .failureReason(this.failureReason)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    public boolean isPending() { return this.status == PaymentStatus.PENDING; }
    public boolean isSuccess() { return this.status == PaymentStatus.SUCCESS; }
}