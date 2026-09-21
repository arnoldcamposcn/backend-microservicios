package com.restaurant.product.infrastructure.messaging.producer;

import com.restaurant.product.domain.port.out.StockEventPublisherPort;
import com.restaurant.product.infrastructure.messaging.config.StockKafkaConfig;
import com.restaurant.product.infrastructure.messaging.event.StockResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class StockEventProducer implements StockEventPublisherPort {

    private final KafkaTemplate<String, StockResultEvent> kafkaTemplate;

    @Override
    public Mono<Void> publishConfirmed(Long orderId, String cartId) {
        return publish(
                StockKafkaConfig.STOCK_CONFIRMED_TOPIC,
                new StockResultEvent(
                        orderId,
                        cartId,
                        "CONFIRMED",
                        null
                )
        );
    }

    @Override
    public Mono<Void> publishRejected(
            Long orderId,
            String cartId,
            String reason) {

        return publish(
                StockKafkaConfig.STOCK_REJECTED_TOPIC,
                new StockResultEvent(
                        orderId,
                        cartId,
                        "REJECTED",
                        reason
                )
        );
    }

    private Mono<Void> publish(
            String topic,
            StockResultEvent event) {

        return Mono.defer(() ->
                        Mono.fromFuture(kafkaTemplate.send(
                                topic,
                                String.valueOf(event.getOrderId()),
                                event
                        ))
                )
                .then();
    }
}
