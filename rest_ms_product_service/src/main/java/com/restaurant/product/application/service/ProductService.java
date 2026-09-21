package com.restaurant.product.application.service;

import com.restaurant.product.domain.exception.InsufficientStockException;
import com.restaurant.product.domain.model.Product;
import com.restaurant.product.domain.model.StockAdjustment;
import com.restaurant.product.domain.port.in.ProductUseCase;
import com.restaurant.product.domain.port.out.ProcessedEventRepositoryPort;
import com.restaurant.product.domain.port.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final ProcessedEventRepositoryPort processedEventRepositoryPort;

    @Override
    public Flux<Product> getProducts() {
        return productRepositoryPort.findAll();
    }

    @Override
    public Mono<Product> getProductById(Long id) {
        return productRepositoryPort.findById(id);
    }

    @Override
    public Mono<Product> createProduct(Product product) {
        return productRepositoryPort.save(product);
    }

    @Override
    public Mono<Product> updateProduct(Long id, Product product) {
        return productRepositoryPort.findById(id)
                .flatMap(existingProduct -> {
                    product.setId(id);
                    return productRepositoryPort.save(product);
                });
    }

    @Override
    public Mono<Void> deleteProduct(Long id) {
        return productRepositoryPort.deleteById(id);
    }

    @Override
    @Transactional
    public Mono<Void> processOrderStock(
            Long orderId,
            List<StockAdjustment> adjustments) {

        return Mono.defer(() -> {
            if (orderId == null) {
                return Mono.error(
                        new IllegalArgumentException("Order id is required")
                );
            }

            if (adjustments == null || adjustments.isEmpty()) {
                return Mono.error(
                        new IllegalArgumentException(
                                "At least one stock adjustment is required"
                        )
                );
            }

            return processedEventRepositoryPort
                    .tryMarkProcessed(orderId)
                    .flatMap(isNewEvent -> {
                        if (!isNewEvent) {
                            return Mono.empty();
                        }

                        return Flux.fromIterable(adjustments)
                                .concatMap(this::applyStockAdjustment)
                                .then();
                    });
        });
    }

    private Mono<Void> applyStockAdjustment(
            StockAdjustment adjustment) {

        if (adjustment.productId() == null
                || adjustment.quantity() == null
                || adjustment.quantity() <= 0) {

            return Mono.error(
                    new IllegalArgumentException(
                            "Product id and a positive quantity are required"
                    )
            );
        }

        return productRepositoryPort
                .decrementStock(
                        adjustment.productId(),
                        adjustment.quantity()
                )
                .flatMap(updated -> {
                    if (updated) {
                        return Mono.empty();
                    }

                    return Mono.error(
                            new InsufficientStockException(
                                    adjustment.productId()
                            )
                    );
                });
    }
}