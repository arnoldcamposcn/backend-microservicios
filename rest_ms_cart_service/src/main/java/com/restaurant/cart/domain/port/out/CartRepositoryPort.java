package com.restaurant.cart.domain.port.out;

import com.restaurant.cart.domain.model.Cart;
import reactor.core.publisher.Mono;

public interface CartRepositoryPort {

    Mono<Cart> findById(String cartId);

    Mono<Cart> save(Cart cart);

    Mono<Void> deleteById(String cartId);
}