package com.restaurant.cart.infrastructure.adapter.in;

import com.restaurant.cart.domain.model.Cart;
import com.restaurant.cart.domain.model.CartItem;
import com.restaurant.cart.domain.port.in.CartUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartUseCase cartUseCase;

    @GetMapping("/{cartId}")
    public Mono<Cart> getCart(@PathVariable String cartId) {
        return cartUseCase.getCart(cartId);
    }

    @PostMapping("/{cartId}/items")
    public Mono<Cart> addItem(
            @PathVariable String cartId,
            @RequestBody CartItem item) {

        return cartUseCase.addItem(cartId, item);
    }

    @PutMapping("/{cartId}/items/{productId}")
    public Mono<Cart> updateItem(
            @PathVariable String cartId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {

        return cartUseCase.updateItem(
                cartId,
                productId,
                quantity
        );
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public Mono<Cart> removeItem(
            @PathVariable String cartId,
            @PathVariable Long productId) {

        return cartUseCase.removeItem(cartId, productId);
    }

    @DeleteMapping("/{cartId}")
    public Mono<Void> clearCart(@PathVariable String cartId) {
        return cartUseCase.clearCart(cartId);
    }
}