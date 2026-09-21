package com.restaurant.order.infrastructure.messaging.consumer;

import com.restaurant.order.domain.model.Order;
import com.restaurant.order.domain.port.in.OrderUseCase;
import com.restaurant.order.infrastructure.messaging.event.StockResultEvent;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StockResultConsumerTest {

    private final OrderUseCase orderUseCase = mock(OrderUseCase.class);
    private final StockResultConsumer consumer =
            new StockResultConsumer(orderUseCase);

    @Test
    void shouldConfirmOrderWhenStockIsConfirmed() {
        when(orderUseCase.updateStatus(10L, "CONFIRMED"))
                .thenReturn(Mono.just(new Order()));

        consumer.consumeConfirmed(event("CONFIRMED", null));

        verify(orderUseCase).updateStatus(10L, "CONFIRMED");
    }

    @Test
    void shouldRejectOrderWhenStockIsRejected() {
        when(orderUseCase.updateStatus(10L, "REJECTED"))
                .thenReturn(Mono.just(new Order()));

        consumer.consumeRejected(event(
                "REJECTED",
                "Insufficient stock"
        ));

        verify(orderUseCase).updateStatus(10L, "REJECTED");
    }

    @Test
    void shouldPropagateOrderUpdateErrors() {
        when(orderUseCase.updateStatus(10L, "CONFIRMED"))
                .thenReturn(Mono.error(
                        new IllegalStateException("Database unavailable")
                ));

        assertThrows(
                IllegalStateException.class,
                () -> consumer.consumeConfirmed(
                        event("CONFIRMED", null)
                )
        );
    }

    private StockResultEvent event(String status, String reason) {
        return new StockResultEvent(
                10L,
                "cart-1",
                status,
                reason
        );
    }
}
