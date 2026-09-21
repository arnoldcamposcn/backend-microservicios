package com.restaurant.order.domain.port.out;

import com.restaurant.order.domain.model.Order;
import reactor.core.publisher.Mono;

public interface OrderEventPublisherPort {

    Mono<Void> publishOrderCreated(Order order);
}
