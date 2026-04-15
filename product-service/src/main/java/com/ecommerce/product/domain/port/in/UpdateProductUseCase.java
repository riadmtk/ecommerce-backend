package com.ecommerce.product.domain.port.in;

import com.ecommerce.product.domain.model.Product;

public interface UpdateProductUseCase {
    Product execute(UpdateProductCommand command);
}