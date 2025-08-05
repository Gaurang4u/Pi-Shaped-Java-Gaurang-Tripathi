# Observability Stack for Spring Boot Microservices - Day 5



## Stack Overview

This project sets up an **observability stack** for microservices using Docker Compose. It includes:

- **Elasticsearch**: Log storage and search engine
- **Logstash**: Log processing pipeline
- **Kibana**: Visualization for logs
- **Prometheus**: Metrics collection
- **Grafana**: Metrics dashboards
- **Zipkin**: Distributed tracing
- **Spring Cloud Sleuth**: Trace instrumentation for Spring Boot

---

## Core Observability Concepts

Understanding observability in microservices requires clear distinction between metrics, logs, and traces, and how tools like **Micrometer**, **Sleuth**, **Zipkin**, **Kibana**, and **Grafana** help you track and debug distributed systems. Below are core questions with practical insights and real-world use cases.

---

### 🔹 What are the differences between metrics, logs, and traces?

| Type     | Purpose                                    | Granularity      | Example |
|----------|--------------------------------------------|------------------|---------|
| Metrics  | Numerical measurements over time           | System-level     | CPU usage, request latency, GC time |
| Logs     | Time-stamped textual events                | Event-level      | “User login failed: 401 Unauthorized” |
| Traces   | End-to-end request lifecycle across services| Request-level    | Trace of an API call from gateway → auth → DB service |

**Use Case:**  
- Metrics help with **alerting** (e.g., high memory usage).
- Logs help with **debugging** ("Why did this transaction fail?").
- Traces help with **performance bottleneck analysis** (which microservice is slow?).

---

### 🔹 What’s the role of Micrometer and how does it enable backend-agnostic metrics?

**Micrometer** is a facade that decouples metric instrumentation from the monitoring backend. You write metrics once using Micrometer, and export to **Prometheus**, **Datadog**, **New Relic**, or **Graphite**, by simply changing the dependency.

**Use Case:**  
If your service runs in multiple environments (local: Prometheus, production: Datadog), you don’t rewrite instrumentation code. Just change `MicrometerRegistry`.

---

### 🔹 How do `traceId` and `spanId` travel across microservice boundaries?

They are propagated through **HTTP headers** (usually `X-B3-TraceId`, `X-B3-SpanId`, etc.) injected by Sleuth or OpenTelemetry.

**Use Case:**  
A frontend request generates `traceId=abc123`. As the request flows from API Gateway → Auth Service → Payment Service, all services log and tag events with this `traceId`, enabling full request tracing in Zipkin/Grafana Tempo.

---

### 🔹 How does Sleuth propagate context in WebClient/RestTemplate?

Spring Cloud Sleuth automatically **injects and extracts tracing headers** in:

- `RestTemplate` via `RestTemplateCustomizer`
- `WebClient` via `ExchangeFilterFunction`

**Use Case:**  
When Service A calls Service B via `WebClient`, Sleuth ensures the trace continues by passing `traceId` in headers, enabling consistent distributed tracing without manual coding.

---

### 🔹 What is the difference between `Timer`, `Gauge`, and `Counter` in Micrometer?

| Type    | Purpose                          | Mutable? | Example |
|---------|----------------------------------|----------|---------|
| Timer   | Measures duration + rate         | ✅        | `@Timed` on method to track execution time |
| Gauge   | Instantaneous measurement        | ✅        | Current thread count in pool |
| Counter | Monotonically increasing value   | ❌        | Number of logins, API hits |

**Use Case:**
- Use `Timer` to monitor method execution (e.g., `processPayment()` time).
- Use `Gauge` for real-time pool size monitoring.
- Use `Counter` to track rate of failed logins over time.

---

### 🔹 What is MDC and how does it help with log correlation?

**MDC (Mapped Diagnostic Context)** stores per-thread context like `traceId`, `userId`, etc., to be automatically included in log patterns.

**Use Case:**
```java
MDC.put("traceId", traceId);
log.info("Order failed");
```

**Log Output:**
```java
INFO [traceId=abc123] Order failed
```

### 🔹 What is the difference between structured vs unstructured logging?

| Type         | Format     | Searchable? | Example                                                   |
|--------------|------------|-------------|-----------------------------------------------------------|
| Structured   | JSON       | ✅           | `{ "level": "INFO", "user": "Aman", "action": "login" }` |
| Unstructured | Plain text | ❌ (harder) | `"User Aman logged in successfully"`                      |

**Use Case:**  
Structured logs allow **Kibana queries** like:

---

---

### 🔹 How do you use Kibana to search for logs tied to a specific `traceId`?

1. Ensure logs are indexed with `traceId` (via MDC or Sleuth).
2. In Kibana Discover tab, search:
```java
traceId:"abc123"
```

3. Visualize logs from all microservices involved in that trace.

**Use Case:**  
Quickly identify if a user request failed in Gateway, Auth, or DB layer without hopping across multiple logs.

---

### 🔹 How can you monitor memory, DB pool, and request error rates in Grafana?

- **Memory:**  
Use `jvm.memory.used` from Micrometer + Prometheus.

- **DB Pool:**  
Expose HikariCP metrics like `hikaricp.connections.active`.

- **Request Error Rate:**  
- Use counters for: http.server.requests{status="5xx"}


**Use Case:**  
Alert if:
- Memory usage > 80%
- DB pool exceeds max size
- Error rate > 2% over 5 mins

---

### 🔹 How does sampling affect Zipkin trace accuracy and performance?

Sampling controls the % of requests that get traced (e.g., 10%).

| Benefit                  | Risk                  |
|--------------------------|------------------------|
| Less performance overhead| May miss key traces    |
| Good for high TPS systems| Incomplete visibility  |

**Use Case:**  
For production systems with 10,000 RPS, tracing only 10% keeps Zipkin performant while still offering insight into bottlenecks. But for debugging incidents, increase sample rate temporarily to 100%.

---

## 🐳 Docker Services

| Service       | Port  | Description                     |
|---------------|-------|---------------------------------|
| Elasticsearch | 9200  | Log storage                     |
| Kibana        | 5601  | UI for Elasticsearch logs       |
| Logstash      | 5000  | Parses and forwards logs        |
| Prometheus    | 9090  | Collects metrics                |
| Grafana       | 3000  | Visualizes Prometheus metrics   |
| Zipkin        | 9411  | Trace visualization             |

---

## Getting Started

### 1. Clone the Repository
```bash
git clone <repo-url>
cd Day5/observability
```

### 2. Start All Containers
```bash
docker-compose up -d
```

### 3. Verify Services
#### Elasticsearch
```bash
    http://localhost:9200
```

- Expected Output:
```bash
{
  "cluster_name": "docker-cluster",
  "tagline": "You Know, for Search"
}
```

#### Kibana
```bash
    http://localhost:5601
    Use it to view Elasticsearch logs.
```

#### Logstash

    Reads from logstash.conf and forwards to Elasticsearch.

#### Prometheus
```bash

    http://localhost:9090
    Go to Status > Targets to verify Spring Boot app is UP.
```

#### Grafana
```bash
    http://localhost:3000
```

- Default login:
```yaml
    Username: admin
    Password: admin
```

####  Zipkin
```bash
    http://localhost:9411
    Click Run Query to view traces.
```
---

## Spring Boot Configuration

Add the following dependencies in your Spring Boot app:

```bash

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
  <groupId>io.micrometer</groupId>
  <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```
---

```yaml
Add to application.yml

management:
  endpoints:
    web:
      exposure:
        include: "*"
  metrics:
    export:
      prometheus:
        enabled: true
  endpoint:
    prometheus:
      enabled: true

spring:
  zipkin:
    base-url: http://localhost:9411
  sleuth:
    sampler:
      probability: 1.0

```
---

## Grafana Dashboard Setup

- Open Grafana → http://localhost:3000
```bash

    Login with admin/admin

    Add Prometheus as a Data Source

        URL: http://prometheus:9090
```
---

## Tracing with Zipkin

Trigger a request to your service:
```bash

curl http://localhost:8080/api/hello

```

Then go to Zipkin → Click Run Query
➡️ You’ll see the trace with timing and microservice path.

---

## Tear Down
```bash

docker-compose down
```





