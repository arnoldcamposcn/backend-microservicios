package com.restaurant.product.infrastructure.adapter.out;

import com.restaurant.product.infrastructure.persistence.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductR2dbcRepository
        extends ReactiveCrudRepository<ProductEntity, Long> {
}