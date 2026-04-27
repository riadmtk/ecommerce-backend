package com.ecommerce.product.domain.port.out;

import java.util.UUID;

public interface ProductEventPublisherPort {
    void publishProductDeleted(UUID productId);
}