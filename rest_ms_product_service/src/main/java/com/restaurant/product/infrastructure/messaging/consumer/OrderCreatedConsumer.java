package com.restaurant.product.infrastructure.messaging.consumer;

import com.restaurant.product.domain.port.in.ProductUseCase;
import com.restaurant.product.infrastructure.messaging.event.OrderCreatedEvent;
import com.restaurant.product.infrastructure.messaging.event.OrderItemEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final ProductUseCase productUseCase;

    @KafkaListener(
            topics = "restaurant.order.created",
            groupId = "product-service"
    )
    public void consume(OrderCreatedEvent event) {

        for (OrderItemEvent item : event.getItems()) {

            productUseCase
                    .decrementStock(
                            item.getProductId(),
                            item.getQuantity()
                    )
                    .subscribe();
        }
    }
}