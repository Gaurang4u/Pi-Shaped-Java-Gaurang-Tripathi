# Microservices Architecture with Eureka Service Discovery

**Author**: Aman Jha

This project demonstrates a complete microservices-based architecture using **Spring Boot**, **Spring Cloud Eureka**, and **Spring Cloud Gateway** for service registration, discovery, and routing.

---

## Project Structure

```plaintext
microservices-eureka-architecture/
├── eureka-server/                 # Eureka registry for service discovery
│   └── src/main/java
├── api-gateway/                  # API Gateway using Spring Cloud Gateway
│   └── src/main/java
├── user-service/                 # User management service
│   └── src/main/java
├── order-service/                # Order management service
│   └── src/main/java
├── pom.xml
└── README.md
```
---

## How It Works

- All services register themselves with Eureka using `@EnableEurekaClient`.
- The API Gateway uses `lb://<service-name>` URI format to route requests dynamically.
- Eureka provides instance registration, discovery, load balancing, and failure detection.

---

## Sample Gateway Route (`application.yml`)

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/users/**
          filters:
            - name: CircuitBreaker
              args:
                name: userCircuitBreaker
                fallbackUri: forward:/fallback/user
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 5
                redis-rate-limiter.burstCapacity: 10
                key-resolver: "#{@userKeyResolver}"
```
---

# Resilience Patterns

## Circuit Breaker

- Helps isolate service failures to prevent cascading issues.
- Routes requests to a fallback URI if a service is unresponsive.
- Implemented using **Resilience4j**.

## Rate Limiting

- Prevents any one client from overloading the system.
- Configured using `RequestRateLimiter` in Gateway (Redis or in-memory).

---

## Service Health with Spring Boot Actuator

Each service exposes the `/actuator/health` endpoint:

- Eureka uses it to decide if a service instance is healthy.
- Ensures only healthy services receive traffic.
- You can also expose `/actuator/info`, `/actuator/metrics`, etc.

### Example Configuration:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
```
---
## Use Cases

- Dynamic routing between services without hardcoding URLs
- Centralized health monitoring using `/actuator/health`
- Rate limiting and circuit breakers via API Gateway
- Load balancing between service instances automatically
- Easily scalable microservices infrastructure

---

## Core Concept Questions (Explained in Simple Terms)

### 1. What’s the difference between client-side and server-side service discovery?

- In **client-side discovery** (like Eureka), the client (e.g., Gateway) queries the registry and picks an instance.
- In **server-side discovery** (like Kubernetes DNS), a load balancer selects the instance, not the client.

### 2. How does Eureka detect dead service instances?

- Services send regular heartbeats to Eureka.
- If Eureka misses a few heartbeats, it assumes the service is down and removes it from the registry.

### 3. Why is the `/actuator/health` endpoint important for service discovery?

- Eureka uses it to check if a service instance is healthy.
- If the endpoint returns unhealthy, Eureka stops routing traffic to it.

### 4. How does `lb://` routing in Gateway work with `DiscoveryClient`?

- The `lb://` prefix signals the Gateway to ask Eureka for service instances.
- Gateway uses a load balancer (default: **round-robin**) to select one of the available instances.

### 5. What happens when a registered service goes down?

- It stops sending heartbeats.
- Eureka deregisters the instance.
- Other services stop routing requests to it automatically.

### 6. Compare Eureka, Consul, and Kubernetes DNS-based discovery

| Feature         | Eureka                 | Consul                 | Kubernetes DNS          |
|----------------|------------------------|------------------------|--------------------------|
| Type            | Centralized registry   | Registry + KV store    | Native DNS-based         |
| Health Checks   | Heartbeats + status    | HTTP/script-based      | Readiness/Liveness probes |
| Integration     | Java-first (Spring)    | Language-agnostic      | Kubernetes-native        |
| Load Balancing  | Client-side            | Client/Server-side     | Server-side via Services |

### 7. How does Spring LoadBalancer choose which instance to call?

- By default, it uses a **round-robin** strategy.
- You can customize it to use other algorithms like **random**, **weighted**, etc.

---

## Prerequisites

- Java 17+
- Maven
- Spring Boot with Spring Cloud
- Docker *(optional, for containerization)*
- Postman *(for testing APIs)*

---

## Running the Project

1. Start the **Eureka Server**
2. Start `user-service` and `order-service` (they auto-register with Eureka)
3. Start the **API Gateway**
4. Test via Postman or browser:
    - [http://localhost:8080/users](http://localhost:8080/users)
    - [http://localhost:8080/orders](http://localhost:8080/orders)

---



