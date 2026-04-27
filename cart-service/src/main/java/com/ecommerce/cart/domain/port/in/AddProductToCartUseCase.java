package com.ecommerce.cart.domain.port.in;
import com.ecommerce.cart.domain.model.Cart;

public interface AddProductToCartUseCase {
    Cart addProductToCart(AddProductToCartCommand command);
}