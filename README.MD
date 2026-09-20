Restaurant Microservices

Backend para la gestión de productos, carritos y órdenes de un restaurante, desarrollado bajo una arquitectura de microservicios.

El proyecto utiliza Java 17, Spring Boot, Spring WebFlux, R2DBC, PostgreSQL, Redis y Spring Cloud Gateway, aplicando Arquitectura Hexagonal y principios SOLID.

Arquitectura

                    ┌─────────────────────┐
                    │    API Gateway      │
                    │       :8080         │
                    └──────────┬──────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
              ▼                ▼                ▼
     ┌────────────────┐ ┌────────────────┐ ┌────────────────┐
     │  Product MS    │ │    Cart MS     │ │    Order MS    │
     │     :8081      │ │     :8082      │ │     :8083      │
     └───────┬────────┘ └───────┬────────┘ └───────┬────────┘
             │                  │                  │
             ▼                  ▼                  ▼
      ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
      │ PostgreSQL  │    │    Redis    │    │ PostgreSQL  │
      │ ms_product  │    │    :6379    │    │  ms_order   │
      └─────────────┘    └─────────────┘    └─────────────┘

Servicios

Servicio	Puerto	Tecnología de persistencia
Product Microservice	8081	PostgreSQL + R2DBC
Cart Microservice	8082	Redis
Order Microservice	8083	PostgreSQL + R2DBC
API Gateway	8080	Spring Cloud Gateway

Flujo

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

Cada microservicio mantiene su propia lógica de negocio y persistencia, favoreciendo el desacoplamiento entre servicios.

Tecnologías

* Java 17
* Spring Boot
* Spring WebFlux
* Spring Data R2DBC
* PostgreSQL
* Redis
* Spring Cloud Gateway
* Gradle
* Arquitectura Hexagonal
* SOLID
* REST API

BY: ARNOLD CAMPOS !
