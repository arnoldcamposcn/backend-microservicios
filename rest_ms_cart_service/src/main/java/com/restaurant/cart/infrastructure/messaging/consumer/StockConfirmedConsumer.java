package com.restaurant.cart.infrastructure.messaging.consumer;

import com.restaurant.cart.domain.port.in.CartUseCase;
import com.restaurant.cart.infrastructure.messaging.event.StockResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class StockConfirmedConsumer {

    private static final Duration PROCESSING_TIMEOUT =
            Duration.ofSeconds(10);

    private final CartUseCase cartUseCase;

    @KafkaListener(
            topics = "restaurant.stock.confirmed",
            groupId = "cart-service"
    )
    public void consume(StockResultEvent event) {
        cartUseCase
                .clearCart(event.getCartId())
                .block(PROCESSING_TIMEOUT);
    }
}
