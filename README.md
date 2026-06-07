# E-Commerce Microservices Platform

![CI/CD Pipeline](https://github.com/noya-prog/spring-boot-microservices-project/actions/workflows/ci-cd.yml/badge.svg)

A full-stack e-commerce application built with a microservices architecture using **Java Spring Boot** for the backend and **React** for the frontend. Services communicate via **REST** and **Apache Kafka**. Authentication is handled by **Keycloak** and fault tolerance by **Resilience4j** circuit breaker.

---

## Architecture Overview

```
                        ┌──────────────────────┐
                        │    React Frontend     │
                        └──────────┬───────────┘
                                   │
                        ┌──────────▼───────────┐
                        │      API Gateway      │
                        │  Keycloak (Auth)      │
                        │  Resilience4j (CB)    │  ← Single entry point
                        └──────┬────────┬───────┘
                               │        │
               ┌───────────────┤        ├───────────────┐
               │               │        │               │
    ┌──────────▼──────┐  ┌─────▼──────────┐  ┌─────────▼────────┐
    │  Product Service │  │  Order Service  │  │ Inventory Service │
    │    MongoDB       │  │  MySQL + Kafka  │  │     MySQL        │
    └─────────────────┘  └───────┬─────┬──┘  └──────────▲───────┘
                                  │     │  RestClient     │
                                  │     └─────────────────┘
                                  │ Kafka (async)
                         ┌────────▼────────┐
                         │    Notification  │
                         │     Service     │
                         │     Kafka       │
                         └─────────────────┘
```

---

## Services

| Service | Description | Database |
|---|---|---|
| **api-gateway** | Routes requests, handles auth (Keycloak) and circuit breaking (Resilience4j) | — |
| **product-service** | Manages product catalog — create, list, and manage products | MongoDB |
| **order-service** | Places orders, checks inventory via RestClient, publishes Kafka events | MySQL |
| **inventory-service** | Tracks stock levels and checks product availability | MySQL |
| **notification-service** | Consumes Kafka events and sends notifications on order placed | — |
| **React application/shop** | Frontend UI for browsing products and placing orders | — |

---

## Tech Stack

**Backend**
- Java 21
- Spring Boot 3
- Spring Cloud Gateway (API Gateway)
- Keycloak (Authentication & Authorization)
- Apache Kafka (async messaging)
- Resilience4j (circuit breaker)
- RestClient + RestClientAdapter + HttpServiceProxyFactory (declarative HTTP client)
- MongoDB (Product Service)
- MySQL (Order Service, Inventory Service)
- Maven

**Frontend**
- React
- JavaScript / HTML / CSS

**Infrastructure**
- Docker & Docker Compose
- GitHub Actions (CI/CD)
- Docker Hub

---

## Getting Started

### Prerequisites
- Java 21
- Docker & Docker Compose
- Maven

### Run with Docker Compose

Each service has its own `docker-compose.yml`. Start the infrastructure (Kafka, databases) for each service:

```bash
# Example: start order-service and its dependencies
cd order-service
docker-compose up -d
```

### Run a service locally

```bash
cd product-service
mvn spring-boot:run
```

### Run the React frontend

```bash
cd "React application/shop"
npm install
npm start
```

---

## CI/CD Pipeline

This project uses **GitHub Actions** for automated build and deployment.

On every push to `main`:
1. **Build & Test** — each service is compiled with Maven in parallel
2. **Build & Push** — Docker images are built and pushed to Docker Hub at `noyaprog/<service-name>:latest`

Docker Hub: [hub.docker.com/u/noyaprog](https://hub.docker.com/u/noyaprog)

---

## Key Design Decisions

- **Synchronous REST communication** — Order Service uses Spring's `RestClient`, `RestClientAdapter`, and `HttpServiceProxyFactory` to call Inventory Service in a declarative, interface-based style to check stock availability before placing an order
- **Asynchronous Kafka messaging** — once an order is placed, Order Service publishes an event to a Kafka topic; Notification Service consumes it independently, keeping the two services fully decoupled
- **Authentication & Authorization** — Keycloak is integrated at the API Gateway level, securing all downstream services without each service needing its own auth logic
- **Circuit breaker** — Resilience4j at the API Gateway prevents cascading failures across services
- **Polyglot persistence** — Product Service uses MongoDB (flexible schema for product data) while Order and Inventory Services use MySQL (relational, transactional data)
- **Independent deployability** — each service has its own `pom.xml` and `Dockerfile`, and can be built and deployed independently

---

## Author

**Neway Tadesse** — [github.com/noya-prog](https://github.com/noya-prog) | [noya-prog.github.io](https://noya-prog.github.io)
