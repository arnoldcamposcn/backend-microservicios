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
        name = "Órdenes",
        description = "Operaciones para crear, consultar y actualizar órdenes"
)
public class OrderController {

    private final OrderUseCase orderUseCase;

    @PostMapping
    @Operation(
            summary = "Crear una orden",
            description = "Registra una orden en estado PENDING_STOCK y publica su creación en Kafka."
    )
    public Mono<Order> createOrder(@RequestBody Order order) {
        return orderUseCase.createOrder(order);
    }

    @GetMapping
    @Operation(
            summary = "Listar órdenes",
            description = "Obtiene todas las órdenes o las filtra por el estado indicado."
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
            summary = "Consultar una orden",
            description = "Obtiene una orden junto con sus productos, total y estado actual."
    )
    public Mono<Order> getOrder(@PathVariable Long id) {
        return orderUseCase.getOrder(id);
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Actualizar el estado",
            description = "Actualiza el estado operativo de una orden existente."
    )
    public Mono<Order> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return orderUseCase.updateStatus(id, status);
    }
}