package com.restaurant.order.domain.port.out;

import com.restaurant.order.domain.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepositoryPort {

    Mono<Order> save(Order order);

    Flux<Order> findAll();

    Flux<Order> findByStatus(String status);

    Mono<Order> findById(Long id);

    Mono<Order> updateStatus(Long id, String status);
}