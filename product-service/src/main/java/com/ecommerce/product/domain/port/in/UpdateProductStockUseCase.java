package com.ecommerce.product.domain.port.in;

import java.util.UUID;

public interface UpdateProductStockUseCase {
    void decreaseStock(UUID productId, int quantity);
    void increaseStock(UUID productId, int quantity);
}