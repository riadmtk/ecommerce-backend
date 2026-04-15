package com.ecommerce.product.domain.port.in;

import java.util.UUID;

public interface HardDeleteProductUseCase {
    void hardDeleteProduct(UUID id);
}