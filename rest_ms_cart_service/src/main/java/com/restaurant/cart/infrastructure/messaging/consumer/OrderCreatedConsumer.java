package com.restaurant.cart.infrastructure.messaging.consumer;

import com.restaurant.cart.domain.port.in.CartUseCase;
import com.restaurant.cart.infrastructure.messaging.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final CartUseCase cartUseCase;

    @KafkaListener(
            topics = "restaurant.order.created",
            groupId = "cart-service"
    )
    public void consume(OrderCreatedEvent event) {

        cartUseCase
                .clearCart(event.getCartId())
                .subscribe();
    }
}