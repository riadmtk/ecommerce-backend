package com.ecommerce.product.domain.port.in;

import com.ecommerce.product.domain.model.Product;
import java.util.UUID;

public interface GetAdminProductUseCase {
    Product getAdminProductById(UUID id);
}