package com.ecommerce.wishlist.domain.port.out;

import com.ecommerce.wishlist.domain.exception.ProductNotFoundException;
import com.ecommerce.wishlist.domain.exception.ProductServiceUnavailableException;

import java.util.UUID;

public interface ProductServiceClientPort {

    /**
     * Validates if a product exists in the catalog.
     *
     * @param productId The ID of the product to validate.
     * @throws ProductNotFoundException if the product does not exist.
     * @throws ProductServiceUnavailableException if the product service is down.
     */
    void validateProductExists(UUID productId) throws ProductNotFoundException, ProductServiceUnavailableException;
}