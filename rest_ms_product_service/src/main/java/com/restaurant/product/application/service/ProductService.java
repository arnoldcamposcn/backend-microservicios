package com.restaurant.product.application.service;

import com.restaurant.product.domain.model.Product;
import com.restaurant.product.domain.port.in.ProductUseCase;
import com.restaurant.product.domain.port.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

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
    public Mono<Product> decrementStock(
            Long productId,
            Integer quantity) {

        return productRepositoryPort.findById(productId)
                .flatMap(product -> {

                    if (product.getStock() < quantity) {
                        return Mono.error(
                                new IllegalStateException(
                                        "Insufficient stock for product: "
                                                + productId
                                )
                        );
                    }

                    int newStock =
                            product.getStock() - quantity;

                    product.setStock(newStock);

                    product.setAvailable(newStock > 0);

                    return productRepositoryPort.save(product);
                });
    }
}