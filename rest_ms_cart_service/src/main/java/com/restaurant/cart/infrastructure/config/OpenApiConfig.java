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
                        .title("Microservicio de Carritos")
                        .description("""
                                API reactiva responsable de administrar los \
                                carritos de compra temporales del restaurante \
                                en Redis. Permite consultar un carrito, agregar \
                                productos, modificar cantidades, retirar \
                                productos y vaciarlo. El carrito se elimina \
                                automáticamente cuando Kafka notifica que el \
                                stock de una orden fue confirmado.
                                """)
                        .version("1.0.0"));
    }
}
