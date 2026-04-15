package com.ecommerce.product.domain.port.in;

import com.ecommerce.product.domain.model.Product;

public interface CreateProductUseCase {
    Product execute(CreateProductCommand command);
}