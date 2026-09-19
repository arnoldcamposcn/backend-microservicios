package com.restaurant.product.infrastructure.adapter.in;

import com.restaurant.product.domain.model.Product;
import com.restaurant.product.domain.port.in.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductUseCase productUseCase;

    @GetMapping
    public Flux<Product> getProducts() {
        return productUseCase.getProducts();
    }

    @GetMapping("/{id}")
    public Mono<Product> getProduct(@PathVariable Long id) {
        return productUseCase.getProductById(id);
    }

    @PostMapping
    public Mono<Product> createProduct(@RequestBody Product product) {
        return productUseCase.createProduct(product);
    }

    @PutMapping("/{id}")
    public Mono<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return productUseCase.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteProduct(@PathVariable Long id) {
        return productUseCase.deleteProduct(id);
    }
}