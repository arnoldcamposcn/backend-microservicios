package com.restaurant.cart.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cartOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Carrito de Pedido")
                        .description("""
                                Administra la selección temporal de productos \
                                que un cliente desea incluir en su pedido. \
                                Permite agregar productos, modificar cantidades, \
                                retirar productos y calcular el total del \
                                carrito. Los carritos se almacenan temporalmente \
                                en Redis y se eliminan únicamente cuando Kafka \
                                confirma el inventario de la orden.
                                """)
                        .version("1.0.0"));
    }
}
