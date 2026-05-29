package com.ecommerce.wishlist.domain.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID productId) {
        super("Product with ID " + productId + " does not exist in the catalog.");
    }
}