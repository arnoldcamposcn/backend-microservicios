package com.restaurant.cart.application.service;

import com.restaurant.cart.domain.model.Cart;
import com.restaurant.cart.domain.model.CartItem;
import com.restaurant.cart.domain.port.in.CartUseCase;
import com.restaurant.cart.domain.port.out.CartRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService implements CartUseCase {

    private final CartRepositoryPort cartRepositoryPort;

    @Override
    public Mono<Cart> getCart(String cartId) {
        return cartRepositoryPort.findById(cartId);
    }

    @Override
    public Mono<Cart> addItem(String cartId, CartItem item) {
        return cartRepositoryPort.findById(cartId)
                .defaultIfEmpty(createCart(cartId))
                .flatMap(cart -> {

                    List<CartItem> items = cart.getItems();

                    items.stream()
                            .filter(existingItem ->
                                    existingItem.getProductId().equals(item.getProductId()))
                            .findFirst()
                            .ifPresentOrElse(
                                    existingItem ->
                                            existingItem.setQuantity(
                                                    existingItem.getQuantity() + item.getQuantity()
                                            ),
                                    () -> items.add(item)
                            );

                    calculateTotal(cart);

                    return cartRepositoryPort.save(cart);
                });
    }

    @Override
    public Mono<Cart> updateItem(
            String cartId,
            Long productId,
            Integer quantity) {

        return cartRepositoryPort.findById(cartId)
                .flatMap(cart -> {

                    cart.getItems().stream()
                            .filter(item -> item.getProductId().equals(productId))
                            .findFirst()
                            .ifPresent(item -> item.setQuantity(quantity));

                    calculateTotal(cart);

                    return cartRepositoryPort.save(cart);
                });
    }

    @Override
    public Mono<Cart> removeItem(
            String cartId,
            Long productId) {

        return cartRepositoryPort.findById(cartId)
                .flatMap(cart -> {

                    cart.getItems()
                            .removeIf(item ->
                                    item.getProductId().equals(productId));

                    calculateTotal(cart);

                    return cartRepositoryPort.save(cart);
                });
    }

    @Override
    public Mono<Void> clearCart(String cartId) {
        return cartRepositoryPort.deleteById(cartId);
    }

    private Cart createCart(String cartId) {
        Cart cart = new Cart();

        cart.setId(cartId);
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);

        return cart;
    }

    private void calculateTotal(Cart cart) {

        BigDecimal total = cart.getItems()
                .stream()
                .map(item ->
                        item.getPrice()
                                .multiply(
                                        BigDecimal.valueOf(item.getQuantity())
                                )
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotal(total);
    }
}