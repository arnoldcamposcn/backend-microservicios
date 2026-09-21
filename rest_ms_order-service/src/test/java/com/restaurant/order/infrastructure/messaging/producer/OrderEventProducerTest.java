package com.restaurant.order.infrastructure.messaging.producer;

import com.restaurant.order.domain.model.Order;
import com.restaurant.order.domain.model.OrderItem;
import com.restaurant.order.infrastructure.messaging.config.KafkaConfig;
import com.restaurant.order.infrastructure.messaging.event.OrderCreatedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderEventProducerTest {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate =
            mock(KafkaTemplate.class);
    private final OrderEventProducer producer =
            new OrderEventProducer(kafkaTemplate);

    @Test
    void shouldMapTheOrderAndWaitForKafka() {
        when(kafkaTemplate.send(
                eq(KafkaConfig.ORDER_CREATED_TOPIC),
                eq("10"),
                any(OrderCreatedEvent.class)
        )).thenReturn(CompletableFuture.completedFuture(null));

        producer.publishOrderCreated(order()).block();

        ArgumentCaptor<OrderCreatedEvent> eventCaptor =
                ArgumentCaptor.forClass(OrderCreatedEvent.class);

        verify(kafkaTemplate).send(
                eq(KafkaConfig.ORDER_CREATED_TOPIC),
                eq("10"),
                eventCaptor.capture()
        );

        OrderCreatedEvent event = eventCaptor.getValue();
        assertEquals(10L, event.getOrderId());
        assertEquals("cart-1", event.getCartId());
        assertEquals(1L, event.getItems().get(0).getProductId());
        assertEquals(2, event.getItems().get(0).getQuantity());
    }

    private Order order() {
        OrderItem item = new OrderItem();
        item.setProductId(1L);
        item.setQuantity(2);

        Order order = new Order();
        order.setId(10L);
        order.setCartId("cart-1");
        order.setItems(List.of(item));

        return order;
    }
}
