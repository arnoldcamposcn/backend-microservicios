package com.restaurant.product.infrastructure.adapter.out;

import com.restaurant.product.domain.model.Product;
import com.restaurant.product.domain.port.out.ProductRepositoryPort;
import com.restaurant.product.infrastructure.persistence.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductR2dbcRepository repository;

    @Override
    public Flux<Product> findAll() {
        return repository.findAll()
                .map(this::toDomain);
    }

    @Override
    public Mono<Product> findById(Long id) {
        return repository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Mono<Product> save(Product product) {
        ProductEntity entity = toEntity(product);

        return repository.save(entity)
                .map(this::toDomain);
    }

    @Override
    public Mono<Boolean> decrementStock(
            Long productId,
            Integer quantity) {

        return repository.decrementStock(productId, quantity)
                .map(updatedRows -> updatedRows > 0);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }

    private Product toDomain(ProductEntity entity) {
        Product product = new Product();

        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setDescription(entity.getDescription());
        product.setPrice(entity.getPrice());
        product.setCategory(entity.getCategory());
        product.setStock(entity.getStock());
        product.setAvailable(entity.getAvailable());

        return product;
    }

    private ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();

        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setCategory(product.getCategory());
        entity.setStock(product.getStock());
        entity.setAvailable(product.getAvailable());

        return entity;
    }
}