package com.ecommerce.search.domain.port.out;

import com.ecommerce.search.domain.model.Product;
import java.util.List;

public interface ProductSearchPort {
    void indexProduct(Product product);
    void removeProduct(String productId);
    List<Product> searchByName(String query);
}