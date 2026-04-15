package com.ecommerce.product.domain.port.out;

import com.ecommerce.product.domain.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
    Product save(Product product);
    List<Product> findAll();
    Optional<Product> findById(UUID id);
    void deleteById(UUID id);
}