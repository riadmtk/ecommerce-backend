package com.ecommerce.search.domain.port.in;
import com.ecommerce.search.domain.model.Product;

public interface SyncProductUseCase {
    void syncProduct(Product product);
    void deleteProduct(String productId);
}