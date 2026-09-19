package com.restaurant.product.domain.port.out;

import com.restaurant.product.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepositoryPort {

    Flux<Product> findAll();

    Mono<Product> findById(Long id);

    Mono<Product> save(Product product);

    Mono<Void> deleteById(Long id);
}