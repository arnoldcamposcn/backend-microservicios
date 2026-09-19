package com.restaurant.order.infrastructure.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderItemR2dbcRepository
        extends ReactiveCrudRepository<OrderItemEntity, Long> {

    Flux<OrderItemEntity> findByOrderId(Long orderId);
}