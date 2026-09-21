package com.restaurant.product.infrastructure.adapter.in;

import com.restaurant.product.domain.model.Product;
import com.restaurant.product.domain.port.in.ProductUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(
        name = "Productos del menú",
        description = "Administración del menú y del inventario del restaurante"
)
public class ProductController {

    private final ProductUseCase productUseCase;

    @GetMapping
    @Operation(
            summary = "Listar productos del menú",
            description = "Obtiene todos los productos disponibles en el catálogo del restaurante."
    )
    public Flux<Product> getProducts() {
        return productUseCase.getProducts();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar un producto",
            description = "Obtiene el detalle, precio, disponibilidad y stock de un producto."
    )
    public Mono<Product> getProduct(@PathVariable Long id) {
        return productUseCase.getProductById(id);
    }

    @PostMapping
    @Operation(
            summary = "Registrar un producto",
            description = "Registra un producto del menú con su precio, categoría y stock inicial."
    )
    public Mono<Product> createProduct(@RequestBody Product product) {
        return productUseCase.createProduct(product);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar producto y stock",
            description = "Actualiza la información comercial y el inventario del producto."
    )
    public Mono<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return productUseCase.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un producto",
            description = "Elimina definitivamente un producto del catálogo."
    )
    public Mono<Void> deleteProduct(@PathVariable Long id) {
        return productUseCase.deleteProduct(id);
    }
}