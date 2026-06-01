package com.ecommerce.order.infrastructure.adapter.in.messaging;

import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderStatus;
import com.ecommerce.order.domain.port.in.GetOrderUseCase;
import com.ecommerce.order.domain.port.in.UpdateOrderStatusUseCase;
import com.ecommerce.order.domain.port.out.OrderEventPublisherPort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final OrderEventPublisherPort orderEventPublisher;
    private final GetOrderUseCase getOrderUseCase;

    //private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(topics = "payment.events", groupId = "order-service-group")
    public void handlePaymentEvent(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        if ("PaymentCompleted".equals(eventType)) {
            // Extraction directe du champ orderId (le message est plat)
            String orderIdStr = (String) event.get("orderId");
            if (orderIdStr == null) {
                log.error("orderId manquant dans l'événement PaymentCompleted");
                return;
            }
            UUID orderId = UUID.fromString(orderIdStr);
            // Éviter le double traitement
            Order order = getOrderUseCase.getById(orderId);
            if (order.getStatus() == OrderStatus.PAID) {
                log.info("Commande {} déjà payée, événement ignoré", orderId);
                return;
            }
            updateOrderStatusUseCase.updateStatus(orderId, OrderStatus.PAID);
            log.info("Commande {} passée à PAID", orderId);
        } /*else if ("PaymentRefunded".equals(eventType)) {
            // Logique existante (inchangée)
            String orderIdStr = (String) event.get("orderId");
            if (orderIdStr == null) {
                log.error("orderId manquant dans l'événement PaymentRefunded");
                return;
            }
            UUID orderId = UUID.fromString(orderIdStr);
            Order order = getOrderUseCase.getById(orderId);
            if (order.getStatus() == OrderStatus.REFUNDED) {
                log.info("Commande {} déjà remboursée, événement ignoré", orderId);
                return;
            }
            Order updated = updateOrderStatusUseCase.updateStatus(orderId, OrderStatus.REFUNDED);
            orderEventPublisher.publishOrderRefunded(updated);
            log.info("Commande {} remboursée via événement Kafka", orderId);
        }*/
    }
}