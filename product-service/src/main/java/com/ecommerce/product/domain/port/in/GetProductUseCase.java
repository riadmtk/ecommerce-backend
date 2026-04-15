package com.ecommerce.product.domain.port.in;

import com.ecommerce.product.domain.model.Product;
import java.util.UUID;

public interface GetProductUseCase {
    Product getProductById(UUID id);
}