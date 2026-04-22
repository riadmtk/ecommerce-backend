package com.ecommerce.order.infrastructure.config;

import com.ecommerce.order.domain.exception.OrderNotFoundException;
import com.ecommerce.order.domain.exception.StockUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    record ErrorResponse(int status, String error, String message, LocalDateTime timestamp, String path) {}

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(404, "Not Found", ex.getMessage(), LocalDateTime.now(), request.getRequestURI())
        );
    }

    @ExceptionHandler(StockUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleStockUnavailable(StockUnavailableException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(409, "Conflict", ex.getMessage(), LocalDateTime.now(), request.getRequestURI())
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(400, "Bad Request", ex.getMessage(), LocalDateTime.now(), request.getRequestURI())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(500, "Internal Server Error", "Une erreur inattendue s'est produite", LocalDateTime.now(), request.getRequestURI())
        );
    }
}