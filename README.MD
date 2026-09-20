Restaurant Microservices

Backend para la gestión de productos, carritos y órdenes de un restaurante, desarrollado bajo una arquitectura de microservicios.

El proyecto utiliza Java 17, Spring Boot, Spring WebFlux, R2DBC, PostgreSQL, Redis, Apache Kafka y Spring Cloud Gateway, aplicando Arquitectura Hexagonal y principios SOLID.

Arquitectura

                         ┌─────────────────────┐
                         │    API Gateway      │
                         │       :8080         │
                         └──────────┬──────────┘
                                    │
              ┌─────────────────────┼─────────────────────┐
              │                     │                     │
              ▼                     ▼                     ▼
     ┌────────────────┐    ┌────────────────┐    ┌────────────────┐
     │   Product MS   │    │    Cart MS     │    │    Order MS    │
     │     :8081      │    │     :8082      │    │     :8083      │
     └───────┬────────┘    └───────┬────────┘    └───────┬────────┘
             │                     │                     │
             ▼                     ▼                     ▼
      ┌─────────────┐       ┌─────────────┐       ┌─────────────┐
      │ PostgreSQL  │       │    Redis    │       │ PostgreSQL  │
      │ ms_product  │       │    :6379    │       │  ms_order   │
      └─────────────┘       └─────────────┘       └─────────────┘
                                                        │
                                                        │
                                                        ▼
                                                  ┌───────────┐
                                                  │   Kafka   │
                                                  │   :9092   │
                                                  └─────┬─────┘
                                                        │
                                      ┌─────────────────┴─────────────────┐
                                      │                                   │
                                      ▼                                   ▼
                               ┌──────────────┐                    ┌──────────────┐
                               │  Product MS  │                    │    Cart MS   │
                               │ Decrement    │                    │  Clear Cart  │
                               │    Stock     │                    │              │
                               └──────────────┘                    └──────────────┘

Servicios

Servicio	Puerto	Persistencia / Comunicación
Product Microservice	8081	PostgreSQL + R2DBC + Kafka Consumer
Cart Microservice	8082	Redis + Kafka Consumer
Order Microservice	8083	PostgreSQL + R2DBC + Kafka Producer
API Gateway	8080	Spring Cloud Gateway
Apache Kafka	9092	Event Broker

Comunicación entre servicios

El API Gateway gestiona las peticiones HTTP provenientes del cliente y las dirige al microservicio correspondiente.

Para las operaciones relacionadas con la creación de órdenes se utiliza Apache Kafka para comunicación asíncrona.

Client
  │
  ▼
API Gateway :8080
  │
  ├──► Product MS :8081 ──► PostgreSQL
  │
  ├──► Cart MS :8082 ─────► Redis
  │
  └──► Order MS :8083 ────► PostgreSQL
                              │
                              │ OrderCreatedEvent
                              ▼
                           Kafka :9092
                              │
                       ┌──────┴──────┐
                       ▼             ▼
                 Product MS      Cart MS
                 Reduce stock    Clear cart

Evento OrderCreatedEvent

Cuando se crea una orden, Order Microservice publica el evento:

restaurant.order.created

El evento contiene información como:

{
  "orderId": 1,
  "cartId": "abc123",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}

Los microservicios consumidores procesan el evento de forma independiente:

* Product MS: reduce el stock de los productos.
* Cart MS: limpia el carrito asociado a la orden.

Cada consumidor utiliza su propio Consumer Group, permitiendo que ambos reciban y procesen el mismo evento.

Flujo principal

1. Client
      │
      ▼
2. API Gateway
      │
      ▼
3. Order MS
      │
      ├──► Guarda Order en PostgreSQL
      │
      └──► Publica OrderCreatedEvent
                    │
                    ▼
                 Kafka
                /     \
               ▼       ▼
        Product MS    Cart MS
        stock - qty   clear cart

Persistencia

Cada microservicio mantiene su propia persistencia:

Product MS ──► PostgreSQL
Cart MS    ──► Redis
Order MS   ──► PostgreSQL

Esto permite mantener el desacoplamiento entre servicios y que cada microservicio sea responsable de sus propios datos.

Arquitectura Hexagonal

Cada microservicio sigue una estructura basada en Arquitectura Hexagonal:

Infrastructure
      │
      ▼
     Port
      │
      ▼
Application
      │
      ▼
    Domain
      │
      ▼
     Port
      │
      ▼
Infrastructure

La lógica de negocio se mantiene independiente de los detalles de infraestructura como PostgreSQL, Redis, HTTP o Kafka.

Tecnologías

* Java 17
* Spring Boot
* Spring WebFlux
* Spring Data R2DBC
* PostgreSQL
* Redis
* Apache Kafka
* Spring Cloud Gateway
* Gradle
* REST API
* Arquitectura Hexagonal
* Principios SOLID
* Comunicación asíncrona basada en eventos

Estructura del proyecto

restaurant-microservices/
│
├── rest_ms_product_service/
├── rest_ms_cart_service/
├── rest_ms_order_service/
└── rest_api_gateway/

⸻

BY: ARNOLD CAMPOS