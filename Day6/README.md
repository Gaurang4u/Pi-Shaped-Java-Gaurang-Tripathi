# Spring Boot Security: Form Login + JWT + Role-Based Access 

A **Task Management System** built using **Spring Boot** that allows users to manage tasks efficiently with features like task creation, updates, deletion, and filtering by status, priority, and project.

---

## Features

- **CRUD Operations** for tasks, projects, and users
- **Filtering by Status, Priority, and Project**
- **Assigning Tasks to Users**
- **User Management with Role-Based Access**
- **Exception Handling with Custom Errors**
- **Spring Data JPA for Database Operations**
- **RESTful API with JSON Responses**
- **Optional Handling for Null Values**

---

## Tech Stack

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Hibernate**
- **H2/PostgreSQL/MySQL (Configurable Database)**
- **Maven**

---

## Project Structure
```
com.code.spring.taskmanagement/
│── src/main/java/com/code/spring/taskmanagement/
│   ├── controller/         # REST Controllers
│   ├── entity/             # JPA Entities
│   ├── repository/         # Spring Data JPA Repositories
│   ├── service/            # Business Logic & Service Layer
│   ├── exception/          # Custom Exception Handling
│   ├── TaskManagementApplication.java  # Main Application Entry
│── src/main/resources/
│   ├── application.properties  # Database & App Configurations
│── pom.xml (Dependencies & Build Configuration)
```

---

## Setup & Installation

### **Prerequisites**
- Java 21
- Maven 3+
- H2 database

### **Steps to Run the Project**
1. **Clone the repository**
   ```sh
   git clone https://github.com/Amaninreal/task-management.git
   cd task-management
   ```
2. **Configure Database** in `src/main/resources/application.properties`
   ```properties
   spring.datasource.url=jdbc:h2:file:./data/bookdb
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   spring.jpa.hibernate.ddl-auto=update
   ```
3. **Build the project**
   ```sh
   mvn clean install
   ```
4. **Run the application**
   ```sh
   mvn spring-boot:run
   ```
5. **Access the API** at `http://localhost:8080`

---

## API Endpoints

### **Task Management APIs**

| Method  | Endpoint                         | Description                  |
|---------|----------------------------------|------------------------------|
| **POST**  | `/tasks`                        | Create a new task            |
| **GET**   | `/tasks`                        | Get all tasks                |
| **GET**   | `/tasks/{id}`                   | Get task by ID               |
| **PUT**   | `/tasks/{id}`                   | Update task                  |
| **DELETE**| `/tasks/{id}`                   | Delete task                  |
| **PATCH** | `/tasks/{id}/status?status=X`   | Update task status           |
| **GET**   | `/tasks/status/{status}`        | Get tasks by status          |
| **GET**   | `/tasks/priority/{priority}`    | Get tasks by priority        |
| **GET**   | `/tasks/project/{projectId}`    | Get tasks by project         |

### **Project Management APIs**

| Method  | Endpoint                         | Description                  |
|---------|----------------------------------|------------------------------|
| **POST**  | `/projects`                     | Create a new project         |
| **GET**   | `/projects`                     | Get all projects             |
| **GET**   | `/projects/{id}`                | Get project by ID            |
| **PUT**   | `/projects/{id}`                | Update project               |
| **DELETE**| `/projects/{id}`                | Delete project               |
| **GET**   | `/projects/user/{userId}`       | Get projects by user ID      |
| **GET**   | `/projects/status/{status}`     | Get projects by status       |
| **GET**   | `/projects/priority/{priority}` | Get projects by priority     |

### **User Management APIs**

| Method  | Endpoint                         | Description                  |
|---------|----------------------------------|------------------------------|
| **POST**  | `/users`                        | Create a new user            |
| **GET**   | `/users`                        | Get all users                |
| **GET**   | `/users/{id}`                   | Get user by ID               |
| **PUT**   | `/users/{id}`                   | Update user                  |
| **DELETE**| `/users/{id}`                   | Delete user                  |
| **GET**   | `/users/role/{role}`            | Get users by role            |
| **PATCH** | `/users/{id}/activate?status=X` | Activate or deactivate user  |

---

## 🛠 Example JSON Requests

### **Create Task**
```json
{
    "title": "Design UI",
    "status": "In Progress",
    "priority": "High",
    "deadline": "2025-04-01",
    "assignedTo": {
        "userId": 2,
        "username": "jane_smith",
        "email": "jane.smith@example.com",
        "role": "USER",
        "active": true
    }
}
```

### **Update Task Status**
```json
{
    "status": "Completed"
}
```

### **Activate/Deactivate User**
```json
PATCH /users/5/activate?status=true
```

### **Response Example**
```json
{
    "taskId": 1,
    "title": "Design UI",
    "status": "Completed",
    "priority": "High",
    "deadline": "2025-04-01",
    "assignedTo": {
        "userId": 2,
        "username": "jane_smith",
        "email": "jane.smith@example.com",
        "role": "USER",
        "active": true
    }
}
```
---
## Core Security Concepts with Use Cases

---

### What is the difference between authentication and authorization?

- **Authentication**: Verifies *who* the user is.
- **Authorization**: Determines *what* the user is allowed to do.

**Use Case**:  
A user logs into a banking portal (authentication). After login, they can check their balance but cannot approve loans (authorization).

---

### How does Spring Security handle the authentication flow for form login?

- Intercepts `/login` requests with `UsernamePasswordAuthenticationFilter`.
- Validates credentials using an `AuthenticationManager`.
- Stores authenticated details in `SecurityContextHolder`.

**Use Case**:  
Employees log in to an HR portal via a login form. Based on their roles, they can view payslips or manage payroll.

---

### How is a stateless JWT flow different from session-based authentication?

- **Session-based**: Server maintains session state for each user.
- **JWT-based**: No server state; JWT token contains user identity and is sent in each request.

**Use Case**:  
A mobile app uses JWT tokens for every API call. The server doesn’t store sessions, making the app scalable and stateless.

---

### How do filters in Spring Security get executed and ordered?

- Spring uses a **filter chain** where each filter executes in a defined sequence.
- Custom filters like `JwtAuthenticationFilter` are registered before/after standard filters.

**Use Case**:  
A JWT filter runs before `UsernamePasswordAuthenticationFilter` to intercept API requests and extract tokens from headers.

---

### What role does the `SecurityContextHolder` play?

- Stores the current `Authentication` object for the request thread.
- Allows access to user identity and roles throughout the application.

**Use Case**:  
After login, business services access the authenticated user from `SecurityContextHolder` to log actions or apply role-based rules.

---

### Why should CSRF be disabled for JWT-based APIs?

- CSRF protection targets cookie-based sessions.
- JWT is passed in headers (not automatically by the browser), making CSRF irrelevant.

**Use Case**:  
A frontend sends JWT in `Authorization` header. Since headers can’t be forged in cross-site requests, CSRF isn’t needed.

---

### What is MDC and how does it help with audit/log tracing?

- MDC (Mapped Diagnostic Context) allows adding contextual data (like `traceId`, `username`) to logs.
- Helps correlate logs for a single request across services.

**Use Case**:  
A request to `/api/user` logs the `traceId` and `username`, making it easy to debug issues and trace user actions across services.

---

### What are the security risks of not validating a JWT token signature?

- JWTs can be tampered with if the signature is not verified.
- Attackers can forge tokens to impersonate users or gain elevated privileges.

**Use Case**:  
If someone modifies the JWT payload to add `ROLE_ADMIN` and the server doesn’t validate the signature, they could access sensitive endpoints.

---

### How can you revoke or expire JWTs?

- Use short expiration times.
- Revoke tokens using blacklists or by rotating the signing secret.

**Use Case**:  
If a user logs out or is deactivated, their JWT can still be used unless it’s expired or blacklisted.

---

### What is the purpose of enabling or customizing CORS?

- CORS allows or restricts browser-based requests from different origins.
- Necessary for enabling frontend-backend communication in modern web apps.

**Use Case**:  
Your frontend app on `http://localhost:3000` calls the backend on `http://localhost:8080`. CORS needs to be enabled to allow this communication.

---
