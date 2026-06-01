package com.ecommerce.cart.application.service;

import java.util.UUID;

public record CartItemEvent(UUID userId, UUID productId, String action, int quantity) {}