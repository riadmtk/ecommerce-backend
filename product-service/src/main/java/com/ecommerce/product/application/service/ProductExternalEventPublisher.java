package com.ecommerce.product.application.service;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.port.out.ExternalEventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "external.kafka.enabled", havingValue = "true")
@RequiredArgsConstructor
public class ProductExternalEventPublisher {

    private final ExternalEventPublisherPort externalPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductCreated(Product product) {
        externalPublisher.publish("product_added", mapProduct(product));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductUpdated(Product product) {
        externalPublisher.publish("price_updated", Map.of(
                "product_id", product.getId().toString(),
                "name", product.getName(),
                "price", product.getPrice()
        ));
    }

    private Map<String, Object> mapProduct(Product product) {
        return Map.of(
                "product_id", product.getId().toString(),
                "name", product.getName(),
                "category", product.getCategory() != null ? product.getCategory() : "",
                "price", product.getPrice(),
                "stock_quantity", product.getStockQuantity()
        );
    }
}