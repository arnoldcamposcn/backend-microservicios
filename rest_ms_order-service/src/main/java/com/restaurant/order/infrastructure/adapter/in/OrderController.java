package com.restaurant.order.infrastructure.adapter.in;

import com.restaurant.order.domain.model.Order;
import com.restaurant.order.domain.port.in.OrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(
        name = "Órdenes del restaurante",
        description = "Registro, consulta y seguimiento de las órdenes realizadas"
)
public class OrderController {

    private final OrderUseCase orderUseCase;

    @PostMapping
    @Operation(
            summary = "Crear una orden desde un carrito",
            description = "Registra la orden en estado PENDING_STOCK y solicita validar su inventario."
    )
    public Mono<Order> createOrder(@RequestBody Order order) {
        return orderUseCase.createOrder(order);
    }

    @GetMapping
    @Operation(
            summary = "Listar órdenes realizadas",
            description = "Obtiene las órdenes del restaurante o las filtra por su estado."
    )
    public Flux<Order> getOrders(
            @RequestParam(required = false) String status) {

        if (status != null) {
            return orderUseCase.getOrdersByStatus(status);
        }

        return orderUseCase.getOrders();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar el detalle de una orden",
            description = "Obtiene una orden junto con sus productos, total y estado actual."
    )
    public Mono<Order> getOrder(@PathVariable Long id) {
        return orderUseCase.getOrder(id);
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Actualizar el estado de una orden",
            description = "Actualiza el estado operativo para dar seguimiento a la orden."
    )
    public Mono<Order> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return orderUseCase.updateStatus(id, status);
    }
}