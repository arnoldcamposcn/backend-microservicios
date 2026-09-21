package com.restaurant.order.infrastructure.messaging.producer;

import com.restaurant.order.domain.model.Order;
import com.restaurant.order.domain.port.out.OrderEventPublisherPort;
import com.restaurant.order.infrastructure.messaging.config.KafkaConfig;
import com.restaurant.order.infrastructure.messaging.event.OrderCreatedEvent;
import com.restaurant.order.infrastructure.messaging.event.OrderItemEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderEventProducer implements OrderEventPublisherPort {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Override
    public Mono<Void> publishOrderCreated(Order order) {
        List<OrderItemEvent> items = order.getItems()
                .stream()
                .map(item -> new OrderItemEvent(
                        item.getProductId(),
                        item.getQuantity()
                ))
                .toList();

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getCartId(),
                items
        );

        return Mono.defer(() ->
                        Mono.fromFuture(kafkaTemplate.send(
                                KafkaConfig.ORDER_CREATED_TOPIC,
                                String.valueOf(event.getOrderId()),
                                event
                        ))
                )
                .then();
    }
}