package com.ecommerce.order.domain.exception;

public class StockUnavailableException extends RuntimeException {
    public StockUnavailableException(String productName) {
        super("Stock unavailable for product: " + productName);
    }
}