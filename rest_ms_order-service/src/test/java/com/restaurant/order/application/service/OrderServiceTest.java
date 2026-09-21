package com.restaurant.order.application.service;

import com.restaurant.order.domain.model.Order;
import com.restaurant.order.domain.model.OrderItem;
import com.restaurant.order.domain.port.out.OrderEventPublisherPort;
import com.restaurant.order.domain.port.out.OrderRepositoryPort;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    private final OrderRepositoryPort orderRepositoryPort =
            mock(OrderRepositoryPort.class);
    private final OrderEventPublisherPort orderEventPublisherPort =
            mock(OrderEventPublisherPort.class);
    private final OrderService orderService = new OrderService(
            orderRepositoryPort,
            orderEventPublisherPort
    );

    @Test
    void shouldPublishTheSavedOrder() {
        Order order = order();

        when(orderRepositoryPort.save(order))
                .thenReturn(Mono.just(order));
        when(orderEventPublisherPort.publishOrderCreated(order))
                .thenReturn(Mono.empty());

        Order result = orderService.createOrder(order).block();

        assertEquals("PENDING_STOCK", result.getStatus());
        assertEquals(new BigDecimal("20.00"), result.getTotal());
        verify(orderEventPublisherPort).publishOrderCreated(order);
    }

    @Test
    void shouldPropagatePublicationErrors() {
        Order order = order();

        when(orderRepositoryPort.save(order))
                .thenReturn(Mono.just(order));
        when(orderEventPublisherPort.publishOrderCreated(order))
                .thenReturn(Mono.error(
                        new IllegalStateException("Kafka unavailable")
                ));

        assertThrows(
                IllegalStateException.class,
                () -> orderService.createOrder(order).block()
        );
    }

    private Order order() {
        OrderItem item = new OrderItem();
        item.setProductId(1L);
        item.setProductName("Product");
        item.setPrice(new BigDecimal("10.00"));
        item.setQuantity(2);

        Order order = new Order();
        order.setId(10L);
        order.setCartId("cart-1");
        order.setItems(List.of(item));

        return order;
    }
}
