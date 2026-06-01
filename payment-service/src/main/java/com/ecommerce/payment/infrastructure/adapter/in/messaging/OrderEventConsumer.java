package com.ecommerce.payment.infrastructure.adapter.in.messaging;

import com.ecommerce.payment.domain.model.PaymentMethod;
import com.ecommerce.payment.domain.port.in.InitiatePaymentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final InitiatePaymentUseCase initiatePaymentUseCase;

    @KafkaListener(
            topics = "${kafka.topics.order-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onOrderCreated(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        if (!"OrderCreated".equals(eventType)) return;

        log.info("Event OrderCreated reçu : orderId={}", event.get("orderId"));

        try {
            UUID orderId = UUID.fromString((String) event.get("orderId"));
            UUID userId = UUID.fromString((String) event.get("userId"));
            BigDecimal amount = new BigDecimal(event.get("totalAmount").toString());
            String currency = (String) event.getOrDefault("currency", "EUR");
            String methodStr = (String) event.getOrDefault("paymentMethod", "STRIPE");
            PaymentMethod method = PaymentMethod.valueOf(methodStr);

            initiatePaymentUseCase.initiate(new InitiatePaymentUseCase.InitiatePaymentCommand(
                    orderId, userId, amount, currency, method
            ));
        } catch (Exception e) {
            log.error("Erreur traitement OrderCreated : {}", e.getMessage(), e);
        }
    }
}