package com.ecommerce.product.domain.port.in;

import com.ecommerce.product.domain.model.Product;
import java.util.List;

public interface GetAllProductsUseCase {
    List<Product> getAllProducts();
}