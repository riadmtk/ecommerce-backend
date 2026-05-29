package com.ecommerce.product.domain.port.out;

import com.ecommerce.product.domain.model.Product;

import java.util.UUID;

public interface ProductEventPublisherPort {
    void publishProductDeleted(UUID productId);
    void publishProductCreated(Product product);
    void publishProductUpdated(Product product);
    void publishProductRestocked(UUID productId, String productName);
}