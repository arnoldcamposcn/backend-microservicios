package com.restaurant.product.domain.port.in;

import com.restaurant.product.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductUseCase {

    Flux<Product> getProducts();

    Mono<Product> getProductById(Long id);

    Mono<Product> createProduct(Product product);

    Mono<Product> updateProduct(Long id, Product product);

    Mono<Void> deleteProduct(Long id);

    Mono<Product> decrementStock(Long productId, Integer quantity);
}