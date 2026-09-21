package com.restaurant.product.infrastructure.messaging.consumer;

import com.restaurant.product.domain.exception.InsufficientStockException;
import com.restaurant.product.domain.model.StockAdjustment;
import com.restaurant.product.domain.port.in.ProductUseCase;
import com.restaurant.product.domain.port.out.StockEventPublisherPort;
import com.restaurant.product.infrastructure.messaging.event.OrderCreatedEvent;
import com.restaurant.product.infrastructure.messaging.event.OrderItemEvent;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderCreatedConsumerTest {

    private final ProductUseCase productUseCase = mock(ProductUseCase.class);
    private final StockEventPublisherPort stockEventPublisherPort =
            mock(StockEventPublisherPort.class);
    private final OrderCreatedConsumer consumer =
            new OrderCreatedConsumer(
                    productUseCase,
                    stockEventPublisherPort
            );

    @Test
    void shouldProcessEveryOrderItemAsOneOperation() {
        List<StockAdjustment> adjustments = List.of(
                new StockAdjustment(1L, 2),
                new StockAdjustment(2L, 1)
        );

        when(productUseCase.processOrderStock(10L, adjustments))
                .thenReturn(Mono.empty());
        when(stockEventPublisherPort.publishConfirmed(10L, "cart-1"))
                .thenReturn(Mono.empty());

        OrderCreatedEvent event = new OrderCreatedEvent(
                10L,
                "cart-1",
                List.of(
                        new OrderItemEvent(1L, 2),
                        new OrderItemEvent(2L, 1)
                )
        );

        consumer.consume(event);

        verify(productUseCase).processOrderStock(10L, adjustments);
        verify(stockEventPublisherPort)
                .publishConfirmed(10L, "cart-1");
    }

    @Test
    void shouldPublishRejectionForInsufficientStock() {
        List<StockAdjustment> adjustments = List.of(
                new StockAdjustment(1L, 2)
        );

        when(productUseCase.processOrderStock(10L, adjustments))
                .thenReturn(Mono.error(
                        new InsufficientStockException(1L)
                ));
        when(stockEventPublisherPort.publishRejected(
                10L,
                "cart-1",
                "Product does not exist or has insufficient stock: 1"
        )).thenReturn(Mono.empty());

        OrderCreatedEvent event = new OrderCreatedEvent(
                10L,
                "cart-1",
                List.of(new OrderItemEvent(1L, 2))
        );

        consumer.consume(event);

        verify(stockEventPublisherPort).publishRejected(
                10L,
                "cart-1",
                "Product does not exist or has insufficient stock: 1"
        );
    }

    @Test
    void shouldPropagateTechnicalErrors() {
        List<StockAdjustment> adjustments = List.of(
                new StockAdjustment(1L, 2)
        );

        when(productUseCase.processOrderStock(10L, adjustments))
                .thenReturn(Mono.error(
                        new IllegalStateException("Database unavailable")
                ));

        OrderCreatedEvent event = new OrderCreatedEvent(
                10L,
                "cart-1",
                List.of(new OrderItemEvent(1L, 2))
        );

        assertThrows(
                IllegalStateException.class,
                () -> consumer.consume(event)
        );
    }
}
