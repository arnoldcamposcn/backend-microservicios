package com.restaurant.product.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Productos")
                        .description("""
                                API reactiva responsable del catálogo y del \
                                inventario del restaurante. Permite crear, \
                                consultar, actualizar y eliminar productos, \
                                administrando su precio, categoría, \
                                disponibilidad y cantidad en stock. También \
                                procesa eventos de órdenes mediante Kafka para \
                                descontar inventario de forma transaccional e \
                                idempotente, y publica el resultado de la \
                                validación de stock.
                                """)
                        .version("1.0.0"));
    }
}