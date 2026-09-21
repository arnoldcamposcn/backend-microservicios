package com.restaurant.product.application.service;

import com.restaurant.product.domain.exception.InsufficientStockException;
import com.restaurant.product.domain.model.StockAdjustment;
import com.restaurant.product.domain.port.out.ProcessedEventRepositoryPort;
import com.restaurant.product.domain.port.out.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    private final ProductRepositoryPort productRepositoryPort =
            mock(ProductRepositoryPort.class);
    private final ProcessedEventRepositoryPort processedEventRepositoryPort =
            mock(ProcessedEventRepositoryPort.class);
    private final ProductService productService = new ProductService(
            productRepositoryPort,
            processedEventRepositoryPort
    );

    @Test
    void shouldIgnoreAnOrderThatWasAlreadyProcessed() {
        when(processedEventRepositoryPort.tryMarkProcessed(10L))
                .thenReturn(Mono.just(false));

        assertDoesNotThrow(() ->
                productService.processOrderStock(
                        10L,
                        List.of(new StockAdjustment(1L, 2))
                ).block()
        );

        verifyNoInteractions(productRepositoryPort);
    }

    @Test
    void shouldApplyEveryStockAdjustment() {
        List<StockAdjustment> adjustments = List.of(
                new StockAdjustment(1L, 2),
                new StockAdjustment(2L, 1)
        );

        when(processedEventRepositoryPort.tryMarkProcessed(10L))
                .thenReturn(Mono.just(true));
        when(productRepositoryPort.decrementStock(1L, 2))
                .thenReturn(Mono.just(true));
        when(productRepositoryPort.decrementStock(2L, 1))
                .thenReturn(Mono.just(true));

        productService.processOrderStock(10L, adjustments).block();

        verify(productRepositoryPort).decrementStock(1L, 2);
        verify(productRepositoryPort).decrementStock(2L, 1);
    }

    @Test
    void shouldFailWhenAProductCannotBeUpdated() {
        when(processedEventRepositoryPort.tryMarkProcessed(10L))
                .thenReturn(Mono.just(true));
        when(productRepositoryPort.decrementStock(1L, 5))
                .thenReturn(Mono.just(false));

        assertThrows(
                InsufficientStockException.class,
                () -> productService.processOrderStock(
                        10L,
                        List.of(new StockAdjustment(1L, 5))
                ).block()
        );
    }
}
