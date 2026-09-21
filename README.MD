Sistema de Gestión de Pedidos para Restaurante

Backend reactivo basado en microservicios para administrar el menú, preparar
carritos de pedido, registrar órdenes, validar inventario y consultar el estado
de los pedidos de un restaurante.

El proyecto utiliza Java 17, Spring Boot, Spring WebFlux, R2DBC, PostgreSQL, Redis, Apache Kafka y Spring Cloud Gateway, aplicando Arquitectura Hexagonal y principios SOLID.

Flujo de negocio

1. El restaurante administra los productos del menú y su inventario.
2. El cliente selecciona productos y cantidades en un carrito de pedido.
3. El carrito se utiliza para registrar una nueva orden.
4. Product MS valida y descuenta el inventario.
5. La orden queda `CONFIRMED` o `REJECTED`.
6. Las órdenes realizadas pueden consultarse y filtrarse por estado.

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
                                                        ▼
                                                ┌──────────────┐
                                                │  Product MS  │
                                                │ Decrement    │
                                                │    Stock     │
                                                └──────┬───────┘
                                                       │
                                         confirmed / rejected
                                                       │
                                              ┌────────┴────────┐
                                              ▼                 ▼
                                       ┌──────────────┐  ┌──────────────┐
                                       │    Cart MS   │  │   Order MS   │
                                       │  Clear Cart  │  │ Update State │
                                       └──────────────┘  └──────────────┘

Servicios

Servicio	Puerto	Persistencia / Comunicación
Productos e Inventario	8081	PostgreSQL + R2DBC + Kafka Consumer/Producer
Carrito de Pedido	8082	Redis + Kafka Consumer
Gestión de Órdenes	8083	PostgreSQL + R2DBC + Kafka Producer/Consumer
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
                              │ restaurant.order.created
                              ▼
                           Kafka :9092
                              │
                              ▼
                         Product MS
                         Reduce stock
                              │
                    ┌─────────┴─────────┐
                    ▼                   ▼
          stock.confirmed        stock.rejected
                    │                   │
               ┌────┴────┐              ▼
               ▼         ▼           Order MS
           Cart MS    Order MS        REJECTED
           Clear      CONFIRMED

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

Product MS consume el evento e intenta reducir el stock de todos los
productos dentro de una transacción.

Si el stock se procesa correctamente, publica:

restaurant.stock.confirmed

Order MS cambia la orden a `CONFIRMED` y Cart MS limpia el carrito.

Si algún producto no tiene stock, Product MS publica:

restaurant.stock.rejected

Order MS cambia la orden a `REJECTED` y el carrito se conserva.

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
      │    status: PENDING_STOCK
      └──► Publica restaurant.order.created
                    │
                    ▼
                 Kafka
                    │
                    ▼
               Product MS
              /           \
             ▼             ▼
    stock.confirmed   stock.rejected
         │                  │
      ┌──┴──┐               ▼
      ▼     ▼            Order MS
    Cart  Order          REJECTED
    clear CONFIRMED

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

Documentación de APIs

Cada microservicio publica su contrato OpenAPI y su interfaz Swagger UI:

* Productos e Inventario: `http://localhost:8081/swagger-ui.html`
* Carrito de Pedido: `http://localhost:8082/swagger-ui.html`
* Gestión de Órdenes: `http://localhost:8083/swagger-ui.html`

La documentación incluye el propósito de cada microservicio y la descripción
de todas sus operaciones REST.

Infraestructura local

El archivo `docker-compose.yml` levanta Kafka y Redis para ejecutar los
microservicios desde el entorno local:

Antes de iniciar todo el proyecto, PostgreSQL debe estar disponible en
`localhost:5432` con las bases `ms_product_db` y `ms_order_db`.

En Windows, el script raíz inicia Kafka, Redis y abre una terminal para cada
microservicio:

```powershell
.\start-all.ps1
```

Si la política de ejecución de PowerShell bloquea el script:

```powershell
powershell -ExecutionPolicy Bypass -File .\start-all.ps1
```

También es posible levantar únicamente la infraestructura:

```bash
docker compose up -d
docker compose ps
```

Kafka queda disponible en `localhost:9092` y Redis en `localhost:6379`.
PostgreSQL no forma parte del Compose: las bases `ms_product_db` y
`ms_order_db`, junto con sus tablas, se administran manualmente.

Para detener la infraestructura:

```bash
docker compose down
```

Los volúmenes `kafka_data` y `redis_data` conservan la información entre
reinicios. Para eliminarlos expresamente se puede ejecutar
`docker compose down -v`.

Estructura del proyecto

restaurant-microservices/
│
├── rest_ms_product_service/
├── rest_ms_cart_service/
├── rest_ms_order_service/
└── rest_api_gateway/

⸻

BY: ARNOLD CAMPOS