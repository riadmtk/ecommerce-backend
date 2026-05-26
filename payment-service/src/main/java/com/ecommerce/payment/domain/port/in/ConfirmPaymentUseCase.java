package com.ecommerce.payment.domain.port.in;

import com.ecommerce.payment.domain.model.Payment;

public interface ConfirmPaymentUseCase {

    record ConfirmPaymentCommand(
            String transactionId,
            boolean success,
            String failureReason
    ) {}

    Payment confirm(ConfirmPaymentCommand command);
}