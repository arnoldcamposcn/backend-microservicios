package com.restaurant.order.infrastructure.messaging.producer;

import com.restaurant.order.infrastructure.messaging.config.KafkaConfig;
import com.restaurant.order.infrastructure.messaging.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {

        kafkaTemplate.send(
                KafkaConfig.ORDER_CREATED_TOPIC,
                String.valueOf(event.getOrderId()),
                event
        );
    }
}