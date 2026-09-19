package com.restaurant.cart.domain.port.in;

import com.restaurant.cart.domain.model.Cart;
import com.restaurant.cart.domain.model.CartItem;
import reactor.core.publisher.Mono;

public interface CartUseCase {

    Mono<Cart> getCart(String cartId);

    Mono<Cart> addItem(String cartId, CartItem item);

    Mono<Cart> updateItem(String cartId, Long productId, Integer quantity);

    Mono<Cart> removeItem(String cartId, Long productId);

    Mono<Void> clearCart(String cartId);
}