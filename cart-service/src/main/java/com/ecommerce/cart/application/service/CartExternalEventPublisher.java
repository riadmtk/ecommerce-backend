package com.ecommerce.cart.application.service;

import com.ecommerce.cart.domain.port.out.ExternalEventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "external.kafka.enabled", havingValue = "true")
@RequiredArgsConstructor
public class CartExternalEventPublisher {

    private final ExternalEventPublisherPort externalPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCartItemAdded(CartItemEvent event) {
        if ("ADDED".equals(event.action())) {
            externalPublisher.publish("cart_item_added", Map.of(
                    "user_id", event.userId().toString(),
                    "product_id", event.productId().toString(),
                    "quantity", event.quantity()
            ));
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCartItemRemoved(CartItemEvent event) {
        if ("REMOVED".equals(event.action())) {
            externalPublisher.publish("cart_item_removed", Map.of(
                    "user_id", event.userId().toString(),
                    "product_id", event.productId().toString()
            ));
        }
    }
}