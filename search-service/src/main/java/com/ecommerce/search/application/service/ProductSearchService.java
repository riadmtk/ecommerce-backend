package com.ecommerce.search.application.service;

import com.ecommerce.search.domain.model.Product;
import com.ecommerce.search.domain.port.in.SearchProductUseCase;
import com.ecommerce.search.domain.port.in.SyncProductUseCase;
import com.ecommerce.search.domain.port.out.ProductSearchPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSearchService implements SearchProductUseCase, SyncProductUseCase {

    private final ProductSearchPort searchPort;

    public ProductSearchService(ProductSearchPort searchPort) {
        this.searchPort = searchPort;
    }

    @Override
    public List<Product> searchProducts(String query) {
        return searchPort.searchByName(query);
    }

    @Override
    public void syncProduct(Product product) {
        searchPort.indexProduct(product);
    }

    @Override
    public void deleteProduct(String productId) {
        searchPort.removeProduct(productId);
    }
}