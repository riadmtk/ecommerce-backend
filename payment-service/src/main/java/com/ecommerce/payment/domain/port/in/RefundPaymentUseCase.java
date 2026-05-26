package com.ecommerce.payment.domain.port.in;

import com.ecommerce.payment.domain.model.Payment;
import java.util.UUID;

public interface RefundPaymentUseCase {
    Payment refund(UUID paymentId);
}