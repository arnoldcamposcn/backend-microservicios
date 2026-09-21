package com.restaurant.product.infrastructure.adapter.out;

import com.restaurant.product.domain.port.out.ProcessedEventRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProcessedEventRepositoryAdapter
        implements ProcessedEventRepositoryPort {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<Boolean> tryMarkProcessed(Long orderId) {
        return databaseClient.sql("""
                        INSERT INTO processed_events (order_id)
                        VALUES (:orderId)
                        ON CONFLICT (order_id) DO NOTHING
                        """)
                .bind("orderId", orderId)
                .fetch()
                .rowsUpdated()
                .map(updatedRows -> updatedRows > 0);
    }
}
