package com.restaurant.cart.infrastructure.adapter.in;

import com.restaurant.cart.domain.model.Cart;
import com.restaurant.cart.domain.model.CartItem;
import com.restaurant.cart.domain.port.in.CartUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(
        name = "Carrito de pedido",
        description = "Preparación temporal de los productos que formarán una orden"
)
public class CartController {

    private final CartUseCase cartUseCase;

    @GetMapping("/{cartId}")
    @Operation(
            summary = "Consultar el carrito de pedido",
            description = "Obtiene los productos seleccionados y el total actual del pedido."
    )
    public Mono<Cart> getCart(@PathVariable String cartId) {
        return cartUseCase.getCart(cartId);
    }

    @PostMapping("/{cartId}/items")
    @Operation(
            summary = "Agregar un producto al pedido",
            description = "Agrega un producto al carrito o incrementa la cantidad seleccionada."
    )
    public Mono<Cart> addItem(
            @PathVariable String cartId,
            @RequestBody CartItem item) {

        return cartUseCase.addItem(cartId, item);
    }

    @PutMapping("/{cartId}/items/{productId}")
    @Operation(
            summary = "Actualizar la cantidad",
            description = "Modifica la cantidad de un producto existente y recalcula el total."
    )
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
    @Operation(
            summary = "Retirar un producto del pedido",
            description = "Retira un producto del carrito y recalcula el total del pedido."
    )
    public Mono<Cart> removeItem(
            @PathVariable String cartId,
            @PathVariable Long productId) {

        return cartUseCase.removeItem(cartId, productId);
    }

    @DeleteMapping("/{cartId}")
    @Operation(
            summary = "Vaciar el carrito de pedido",
            description = "Elimina completamente la selección temporal almacenada en Redis."
    )
    public Mono<Void> clearCart(@PathVariable String cartId) {
        return cartUseCase.clearCart(cartId);
    }
}