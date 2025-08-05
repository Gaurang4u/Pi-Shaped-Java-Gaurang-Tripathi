# Microservices Workshop Day 1 - Aman Jha

The project focuses on building a cloud-native e-commerce backend using **Spring WebFlux**, **gRPC**, **R2DBC**, **Redis**, **SSE**, and **WebSocket** technologies.

---

## ✅ Core Concept Questions (with Use Cases)

### 1. What are the differences between Mono and Flux, and where did you use each?

- **Mono**: Represents 0 or 1 asynchronous value.
- **Flux**: Represents 0 to N asynchronous values (stream).
- **Use Cases**:
    - `Mono<User>` → Fetch a single user by ID.
    - `Flux<Product>` → Stream product catalog or live cart updates.

---

### 2. How does R2DBC differ from traditional JDBC?

| JDBC (Blocking)         | R2DBC (Non-Blocking Reactive) |
|--------------------------|-------------------------------|
| Uses blocking I/O calls  | Fully asynchronous I/O        |
| Tied to servlet thread   | Uses reactive event loop      |
| Not ideal for WebFlux    | Native fit for reactive stack |

- **Use Case**: We use R2DBC for non-blocking DB calls in our WebFlux-based REST and SSE APIs.

---

### 3. How does Spring WebFlux routing differ from @RestController?

- `@RestController` uses **annotation-based mapping**.
- WebFlux **functional routing** uses `RouterFunction` and `HandlerFunction` for better control and functional purity.

**Use Case**: `UserRouter` is implemented using `RouterFunction` for custom request chaining and lightweight routing logic.

---

### 4. What are the advantages of using @Transactional with R2DBC (or why not)?

- Traditional `@Transactional` uses thread-local storage—not safe in reactive.
- Use `@Transactional` with **caution** in reactive code.
- **Better Alternative**: Use `DatabaseClient.inTransaction()` explicitly for reactive-safe transactions.

**Use Case**: For multi-step operations like **checkout and payment**, prefer programmatic transaction boundaries.

---

### 5. Optimistic vs Pessimistic Locking — Differences and Use Cases?

| Lock Type     | Description                              | When to Use                           |
|---------------|------------------------------------------|----------------------------------------|
| Optimistic    | Version field; fail if conflict occurs   | Low contention systems (cart updates)  |
| Pessimistic   | Locks row until txn completes            | High contention systems (inventory)    |

---

### 6. How does session management work using Redis in reactive apps?

- Store session data in **Redis** using `ReactiveRedisOperations`.
- Use `@EnableRedisWebSession` for reactive session support.
- Enables **stateless and distributed session sharing**.

**Use Case**: Tracks user carts or auth tokens across load-balanced nodes.

---

### 7. How did you expose and verify SSE updates?

- Used `Flux<ServerSentEvent<CartUpdate>>` to stream cart changes.
- Validated using `WebTestClient` with `StepVerifier` to assert event flow.
- Events consumed by UI for **live cart refresh**.

---

### 8. What is the role of Swagger/OpenAPI in CI/CD and API consumer tooling?

- Auto-generates API docs from codebase.
- Tools like **Swagger UI**, **Postman**, or **OpenAPI Generator** use this spec.
- Enables **mock testing**, contract validation, and **client SDK generation**.

**CI/CD Use**: Validates schema changes and prevents breaking API changes during PR builds.

---

### 9. How does WebTestClient differ from MockMvc in testing?

| Feature         | WebTestClient                | MockMvc                      |
|------------------|------------------------------|-------------------------------|
| Stack            | Reactive (WebFlux)           | Servlet (Spring MVC)         |
| Execution Model  | Non-blocking                 | Blocking                     |
| Used For         | Testing WebFlux apps         | Testing Spring MVC apps      |

---

### 10. Compare WebSocket, SSE, and RSocket for real-time data needs

| Protocol   | Type        | Best For                              |
|------------|-------------|----------------------------------------|
| SSE        | Server → Client (Unidirectional) | Live feed updates like cart, notifications |
| WebSocket  | Full-Duplex | Chat systems, collaborative editing   |
| RSocket    | Reactive streams over TCP/WebSockets | Event-driven microservices, backpressure handling |

---
