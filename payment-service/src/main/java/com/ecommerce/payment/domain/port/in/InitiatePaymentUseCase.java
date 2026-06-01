package com.ecommerce.payment.domain.port.in;

import com.ecommerce.payment.domain.model.Payment;
import com.ecommerce.payment.domain.model.PaymentMethod;
import java.math.BigDecimal;
import java.util.UUID;

public interface InitiatePaymentUseCase {

    record InitiatePaymentCommand(
            UUID orderId,
            UUID userId,
            BigDecimal amount,
            String currency,
            PaymentMethod paymentMethod
    ) {}

    Payment initiate(InitiatePaymentCommand command);
    Payment confirmPayment(UUID paymentId);
}