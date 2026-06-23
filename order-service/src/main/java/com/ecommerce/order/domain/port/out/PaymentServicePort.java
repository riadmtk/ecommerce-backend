package com.ecommerce.order.domain.port.out;

import com.ecommerce.order.infrastructure.adapter.out.rest.dto.PaymentResponse;

import java.util.UUID;

public interface PaymentServicePort {
    PaymentResponse getPaymentByOrderId(UUID orderId);
    void refundPayment(UUID paymentId, String reason);
}
