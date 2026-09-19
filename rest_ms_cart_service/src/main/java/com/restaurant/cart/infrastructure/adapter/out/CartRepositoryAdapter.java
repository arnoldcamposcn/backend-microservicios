package com.restaurant.cart.infrastructure.adapter.out;

import com.restaurant.cart.domain.model.Cart;
import com.restaurant.cart.domain.port.out.CartRepositoryPort;
import com.restaurant.cart.infrastructure.persistence.CartEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CartRepositoryAdapter implements CartRepositoryPort {

    private final ReactiveRedisTemplate<String, CartEntity> redisTemplate;

    @Override
    public Mono<Cart> findById(String cartId) {
        return redisTemplate.opsForValue()
                .get(buildKey(cartId))
                .map(this::toDomain);
    }

    @Override
    public Mono<Cart> save(Cart cart) {

        CartEntity entity = toEntity(cart);

        return redisTemplate.opsForValue()
                .set(buildKey(cart.getId()), entity)
                .thenReturn(cart);
    }

    @Override
    public Mono<Void> deleteById(String cartId) {
        return redisTemplate.opsForValue()
                .delete(buildKey(cartId))
                .then();
    }

    private String buildKey(String cartId) {
        return "cart:" + cartId;
    }

    private Cart toDomain(CartEntity entity) {

        Cart cart = new Cart();

        cart.setId(entity.getId());
        cart.setItems(entity.getItems());
        cart.setTotal(entity.getTotal());

        return cart;
    }

    private CartEntity toEntity(Cart cart) {

        CartEntity entity = new CartEntity();

        entity.setId(cart.getId());
        entity.setItems(cart.getItems());
        entity.setTotal(cart.getTotal());

        return entity;
    }
}