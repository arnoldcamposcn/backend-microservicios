package com.restaurant.order.infrastructure.messaging.consumer;

import com.restaurant.order.domain.port.in.OrderUseCase;
import com.restaurant.order.infrastructure.messaging.event.StockResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class StockResultConsumer {

    private static final Duration PROCESSING_TIMEOUT =
            Duration.ofSeconds(10);

    private final OrderUseCase orderUseCase;

    @KafkaListener(
            topics = "restaurant.stock.confirmed",
            groupId = "order-service-stock"
    )
    public void consumeConfirmed(StockResultEvent event) {
        updateStatus(event.getOrderId(), "CONFIRMED");
    }

    @KafkaListener(
            topics = "restaurant.stock.rejected",
            groupId = "order-service-stock"
    )
    public void consumeRejected(StockResultEvent event) {
        updateStatus(event.getOrderId(), "REJECTED");
    }

    private void updateStatus(Long orderId, String status) {
        orderUseCase
                .updateStatus(orderId, status)
                .then()
                .block(PROCESSING_TIMEOUT);
    }
}
