package com.ecommerce.product.infrastructure.adapter.in.messaging;

import com.ecommerce.product.domain.port.in.UpdateProductStockUseCase;
import com.ecommerce.product.infrastructure.adapter.in.messaging.dto.OrderCreatedEvent;
import com.ecommerce.product.infrastructure.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    // On injecte le Port d'entrée de notre Domaine !
    private final UpdateProductStockUseCase updateProductStockUseCase;

    @KafkaListener(topics = KafkaConfig.TOPIC_ORDER_CREATED, groupId = "product-service-group")
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Événement Kafka reçu : Nouvelle commande {} pour le produit {}. Quantité : {}",
                event.orderId(), event.productId(), event.quantity());

        try {
            // L'Adaptateur fait son travail : il traduit l'événement Web/Kafka en appel métier
            updateProductStockUseCase.decreaseStock(event.productId(), event.quantity());
            log.info("Stock mis à jour avec succès pour le produit {}", event.productId());

        } catch (Exception e) {
            // Ici, en production, on enverrait l'erreur dans un "Dead Letter Queue" (DLQ)
            // ou on déclencherait un événement "OrderFailedEvent" pour annuler la commande.
            log.error("Erreur lors de la mise à jour du stock : {}", e.getMessage());
        }
    }
}