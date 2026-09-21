package com.restaurant.product.domain.port.out;

import reactor.core.publisher.Mono;

public interface StockEventPublisherPort {

    Mono<Void> publishConfirmed(Long orderId, String cartId);

    Mono<Void> publishRejected(
            Long orderId,
            String cartId,
            String reason
    );
}
