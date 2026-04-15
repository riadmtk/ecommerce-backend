package com.ecommerce.product.infrastructure.adapter.in.messaging.dto;

import java.util.UUID;

// C'est ce que le service Commande va envoyer dans le tuyau Kafka
public record OrderCreatedEvent(
        UUID orderId,
        UUID productId,
        int quantity
) {}