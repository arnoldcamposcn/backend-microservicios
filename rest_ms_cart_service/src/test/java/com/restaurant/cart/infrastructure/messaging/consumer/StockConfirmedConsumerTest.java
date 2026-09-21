package com.restaurant.cart.infrastructure.messaging.consumer;

import com.restaurant.cart.domain.port.in.CartUseCase;
import com.restaurant.cart.infrastructure.messaging.event.StockResultEvent;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StockConfirmedConsumerTest {

    private final CartUseCase cartUseCase = mock(CartUseCase.class);
    private final StockConfirmedConsumer consumer =
            new StockConfirmedConsumer(cartUseCase);

    @Test
    void shouldClearCartAfterStockConfirmation() {
        when(cartUseCase.clearCart("cart-1"))
                .thenReturn(Mono.empty());

        consumer.consume(stockConfirmedEvent());

        verify(cartUseCase).clearCart("cart-1");
    }

    @Test
    void shouldPropagateCartProcessingErrors() {
        when(cartUseCase.clearCart("cart-1"))
                .thenReturn(Mono.error(
                        new IllegalStateException("Redis unavailable")
                ));

        assertThrows(
                IllegalStateException.class,
                () -> consumer.consume(stockConfirmedEvent())
        );
    }

    private StockResultEvent stockConfirmedEvent() {
        return new StockResultEvent(
                10L,
                "cart-1",
                "CONFIRMED",
                null
        );
    }
}
