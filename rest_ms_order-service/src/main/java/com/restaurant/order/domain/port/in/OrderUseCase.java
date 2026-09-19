package com.restaurant.order.domain.port.in;

import com.restaurant.order.domain.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderUseCase {

    Mono<Order> createOrder(Order order);

    Flux<Order> getOrders();

    Flux<Order> getOrdersByStatus(String status);

    Mono<Order> getOrder(Long id);

    Mono<Order> updateStatus(Long id, String status);
}