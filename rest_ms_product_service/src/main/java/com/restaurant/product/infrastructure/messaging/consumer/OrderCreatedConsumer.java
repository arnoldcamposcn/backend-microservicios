package com.restaurant.product.infrastructure.messaging.consumer;

import com.restaurant.product.domain.exception.InsufficientStockException;
import com.restaurant.product.domain.model.StockAdjustment;
import com.restaurant.product.domain.port.in.ProductUseCase;
import com.restaurant.product.domain.port.out.StockEventPublisherPort;
import com.restaurant.product.infrastructure.messaging.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private static final Duration PROCESSING_TIMEOUT = Duration.ofSeconds(10);

    private final ProductUseCase productUseCase;
    private final StockEventPublisherPort stockEventPublisherPort;

    @KafkaListener(
            topics = "restaurant.order.created",
            groupId = "product-service"
    )
    public void consume(OrderCreatedEvent event) {
        var adjustments = event.getItems()
                .stream()
                .map(item -> new StockAdjustment(
                        item.getProductId(),
                        item.getQuantity()
                ))
                .toList();

        productUseCase
                .processOrderStock(event.getOrderId(), adjustments)
                .then(Mono.defer(() ->
                        stockEventPublisherPort.publishConfirmed(
                                event.getOrderId(),
                                event.getCartId()
                        )
                ))
                .onErrorResume(
                        InsufficientStockException.class,
                        error -> stockEventPublisherPort.publishRejected(
                                event.getOrderId(),
                                event.getCartId(),
                                error.getMessage()
                        )
                )
                .block(PROCESSING_TIMEOUT);
    }
}