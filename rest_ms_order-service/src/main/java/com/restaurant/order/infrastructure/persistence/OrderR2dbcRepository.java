package com.restaurant.order.infrastructure.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderR2dbcRepository
        extends ReactiveCrudRepository<OrderEntity, Long> {

    Flux<OrderEntity> findByStatus(String status);
}