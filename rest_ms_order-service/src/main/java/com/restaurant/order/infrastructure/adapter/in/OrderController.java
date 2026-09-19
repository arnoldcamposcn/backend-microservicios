package com.restaurant.order.infrastructure.adapter.in;

import com.restaurant.order.domain.model.Order;
import com.restaurant.order.domain.port.in.OrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderUseCase orderUseCase;

    @PostMapping
    public Mono<Order> createOrder(@RequestBody Order order) {
        return orderUseCase.createOrder(order);
    }

    @GetMapping
    public Flux<Order> getOrders(
            @RequestParam(required = false) String status) {

        if (status != null) {
            return orderUseCase.getOrdersByStatus(status);
        }

        return orderUseCase.getOrders();
    }

    @GetMapping("/{id}")
    public Mono<Order> getOrder(@PathVariable Long id) {
        return orderUseCase.getOrder(id);
    }

    @PatchMapping("/{id}/status")
    public Mono<Order> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return orderUseCase.updateStatus(id, status);
    }
}