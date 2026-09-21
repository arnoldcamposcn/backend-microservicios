package com.restaurant.product.infrastructure.adapter.out;

import com.restaurant.product.infrastructure.persistence.ProductEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ProductR2dbcRepository
        extends ReactiveCrudRepository<ProductEntity, Long> {

    @Modifying
    @Query("""
            UPDATE products
            SET stock = stock - :quantity,
                available = (stock - :quantity) > 0
            WHERE id = :productId
              AND stock >= :quantity
            """)
    Mono<Integer> decrementStock(Long productId, Integer quantity);
}