package com.restaurant.product.domain.port.out;

import reactor.core.publisher.Mono;

public interface ProcessedEventRepositoryPort {

    Mono<Boolean> tryMarkProcessed(Long orderId);
}
