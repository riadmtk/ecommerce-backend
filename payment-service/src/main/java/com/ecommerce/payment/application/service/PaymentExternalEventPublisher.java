package com.ecommerce.payment.application.service;

import com.ecommerce.payment.domain.model.Payment;
import com.ecommerce.payment.domain.port.out.ExternalEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.kafka.enabled", havingValue = "true")
public class PaymentExternalEventPublisher {

    private final ExternalEventPublisherPort externalEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        Payment payment = event.payment();
        log.info("[ExternalKafka] onPaymentSuccess paymentId={}", payment.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("payment_id", payment.getId().toString());
        data.put("order_id", payment.getOrderId().toString());
        data.put("amount", payment.getAmount());
        data.put("currency", payment.getCurrency());
        data.put("customer_id", payment.getUserId().toString());
        data.put("transaction_id", payment.getTransactionId());

        externalEventPublisher.publishEvent("payment_success", data);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentFailed(PaymentFailedEvent event) {
        Payment payment = event.payment();
        log.info("[ExternalKafka] onPaymentFailed paymentId={}", payment.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("payment_id", payment.getId().toString());
        data.put("order_id", payment.getOrderId().toString());
        data.put("amount", payment.getAmount());
        data.put("currency", payment.getCurrency());
        data.put("customer_id", payment.getUserId().toString());
        data.put("reason", payment.getFailureReason());

        externalEventPublisher.publishEvent("payment_failed", data);
    }

    // ── Records événements Spring ──────────────────────────────────────────
    public record PaymentSuccessEvent(Payment payment) {}
    public record PaymentFailedEvent(Payment payment) {}
}