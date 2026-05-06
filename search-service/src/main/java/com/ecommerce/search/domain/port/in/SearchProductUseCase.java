package com.ecommerce.search.domain.port.in;
import com.ecommerce.search.domain.model.Product;
import java.util.List;

public interface SearchProductUseCase {
    List<Product> searchProducts(String query);
}