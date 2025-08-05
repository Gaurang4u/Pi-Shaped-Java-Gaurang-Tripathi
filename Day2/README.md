# Aman Jha

## 📅 Day 2 - Spring Cloud Gateway & Config Server

---

### 🧩 Objective:
Design and deploy a microservice architecture using:
- ✅ Spring Cloud Gateway for routing
- ✅ Spring Cloud Config Server for centralized configuration
- ✅ Role-based security
- ✅ Dynamic config refresh
- ✅ Logging with filters

---

### 🗂️ Folder Structure

    ```bash
    Microservices_workshop-Aman_Jha/
    └── day2/
    ├── product-service/
    ├── order-service/
    ├── spring-cloud-gateway/
    
    ```
---

### 🔗 External Config Repository

This project uses a centralized config repository managed via Spring Cloud Config Server.

📁 Public GitHub config repo:
**[https://github.com/Amaninreal/config-repo](https://github.com/Amaninreal/config-repo)**

It includes:
- `product-service.yml`
- `order-service.yml`
- `spring-cloud-gateway.yml`

---

### 🧪 How to Run

1. **Start Config Server**
   ```bash
   cd config-server
   ./mvnw spring-boot:run
   ```
2. **Start Product & Order Services**
    ```bash
    cd product-service
    ./mvnw spring-boot:run
    
    cd order-service
    ./mvnw spring-boot:run
    
    ```

3. **Start Spring Cloud Gateway**
    ```bash
    cd spring-cloud-gateway
    ./mvnw spring-boot:run
   ```
---
## ✅ Submission Checklist

| Task                                                       | Status |
|------------------------------------------------------------|--------|
| Spring Cloud Config Server loads from Git                  | ✅     |
| GET /products/** routes to product-service                 | ✅     |
| GET /orders/** routes to order-service                     | ✅     |
| Dynamic config reload via POST /actuator/refresh           | ✅     |
| Gateway logs incoming requests via custom filter           | ✅     |
| /admin/** route is protected with ROLE_ADMIN               | ✅     |

---

## 🔐 Sample Auth Users

| Username | Password  | Role       |
|----------|-----------|------------|
| admin    | admin123  | ROLE_ADMIN |
| user     | user123   | ROLE_USER  |

➡️ **Try accessing:**  
`http://localhost:8080/admin/info`  
You’ll be prompted for credentials.

---

## 📘 Core Concept Questions

### 1. What is the purpose of `bootstrap.yml` in a config client?
`bootstrap.yml` is loaded **before** `application.yml`. It configures properties required during the **bootstrap phase**, like config server URL and app name.

---

### 2. How does `@RefreshScope` work and when should it be used?
It allows beans to be **refreshed at runtime** when `/actuator/refresh` is triggered. Use it for beans that should reload values without restarting the app.

---

### 3. What’s the difference between static and dynamic route configuration in Gateway?

- **Static**: Hardcoded in `application.yml`
- **Dynamic**: Fetched from config server (YAML in Git), database, or service discovery

---

### 4. How does rate limiting in Spring Cloud Gateway work internally?
Rate limiting (e.g., with RedisRateLimiter) uses the **token bucket algorithm**. It restricts requests per second and burst capacity using Redis backend.

---

### 5. How do you test a configuration change without restarting services?

- Edit properties in the **config Git repo**
- Call `POST /actuator/refresh` on the service
- Beans with `@RefreshScope` reflect updated values

---

### 6. What’s the difference between global and per-route filters in Gateway?

- **Global Filters**: Apply to **all routes**
- **Per-route Filters**: Apply only to specific routes as defined in config

---

### 7. How does Spring Cloud Bus enhance dynamic config refresh?
It uses **message brokers** like RabbitMQ/Kafka to **broadcast refresh events** to all services, so no need to manually call `/actuator/refresh`.

---

### 8. How is JWT verified at the Gateway level?
JWT tokens are parsed and validated via **security filters**, which:
- Check the token’s signature
- Extract claims (e.g., roles)
- Enforce role-based access control

---

### 9. What’s the role of service discovery when routing via Gateway?
Instead of hardcoded URLs, use **service discovery (like Eureka)**:
```yaml
uri: lb://product-service
````
---
### 10. Why prefer centralized config and not embedded .yml?
Centralized configuration:
- Enables live updates

- Promotes consistency

- Avoids config duplication

- Works seamlessly with Spring Cloud Config + Spring cloud Bus
