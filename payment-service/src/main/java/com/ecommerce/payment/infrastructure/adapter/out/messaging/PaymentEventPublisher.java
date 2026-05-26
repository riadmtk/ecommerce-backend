package com.ecommerce.payment.infrastructure.adapter.out.messaging;

import com.ecommerce.payment.domain.model.Payment;
import com.ecommerce.payment.domain.port.out.PaymentEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher implements PaymentEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.payment-events}")
    private String paymentTopic;

    @Override
    public void publishPaymentSucceeded(Payment payment) {
        publish("PaymentSucceeded", payment);
    }

    @Override
    public void publishPaymentFailed(Payment payment) {
        publish("PaymentFailed", payment);
    }

    @Override
    public void publishPaymentRefunded(Payment payment) {
        publish("PaymentRefunded", payment);
    }

    @Override
    public void publishPaymentCompleted(Payment payment) {
        // Unifier le format et le topic : utiliser la même méthode publish
        publish("PaymentCompleted", payment);
    }

    private void publish(String eventType, Payment payment) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", eventType);
        event.put("paymentId", payment.getId().toString());
        event.put("orderId", payment.getOrderId().toString());
        event.put("userId", payment.getUserId().toString());
        event.put("amount", payment.getAmount());
        event.put("currency", payment.getCurrency());
        event.put("status", payment.getStatus().name());
        event.put("transactionId", payment.getTransactionId());
        event.put("occurredAt", LocalDateTime.now().toString());

        kafkaTemplate.send(paymentTopic, payment.getOrderId().toString(), event);
        log.info("Event {} publié pour orderId={}", eventType, payment.getOrderId());
    }
}
