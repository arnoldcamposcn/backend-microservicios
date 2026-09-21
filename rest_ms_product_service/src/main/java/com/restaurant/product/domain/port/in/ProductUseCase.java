package com.restaurant.product.domain.port.in;

import com.restaurant.product.domain.model.Product;
import com.restaurant.product.domain.model.StockAdjustment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductUseCase {

    Flux<Product> getProducts();

    Mono<Product> getProductById(Long id);

    Mono<Product> createProduct(Product product);

    Mono<Product> updateProduct(Long id, Product product);

    Mono<Void> deleteProduct(Long id);

    Mono<Void> processOrderStock(
            Long orderId,
            List<StockAdjustment> adjustments
    );
}