# Insurance Management System

A professional, interview-ready full-stack **Insurance Management System** built with **Java 17**, **Spring Boot**, **Spring Data JPA**, **MySQL**, and a **Vanilla HTML/CSS/JavaScript SPA Frontend** communicating via REST APIs using the **Fetch API**.

---

## 🛡️ Project Overview & Architecture

The application adopts a clean, layered architecture designed in alignment with **SOLID design principles**, utilizing **Constructor-based Dependency Injection** for loose coupling and ease of testing.

```
[Client App (Vanilla HTML/CSS/JS)]
         │
         │ HTTP / Fetch API (with X-Auth-Token Header)
         ▼
[Authentication Interceptor (Custom HandlerInterceptor)] ─── (Blocks unauthorized access)
         │
         ▼
[Controller Layer (REST Endpoints / Validation)]
         │
         ▼
[Service Layer (Business Logic & Transactions)]
         │
         ▼
[Repository Layer (Spring Data JPA / MySQL DB)]
```

### Module Responsibilities:
1.  **Customer Management**: Handles demographic information, birthdates, active statuses, and cascade rules.
2.  **Policy Management**: Configures terms, coverage lengths, and establishes a Many-to-One relational mapping to Customers.
3.  **Lead Management**: Monitors prospects and maps them to agent pipelines.

---

## 🔑 Custom Authentication Mechanism

Instead of heavy security wrappers like Spring Security, this project implements a custom authorization filter using a Spring `HandlerInterceptor`.

*   **Header Target**: Checks the `X-Auth-Token` header.
*   **Access Matrix**:
    *   `ADMIN` token: Permits `GET`, `POST`, `PUT`, and `DELETE`.
    *   `AGENT` token: Permits `GET`, `POST`, and `PUT`. Attempts to `DELETE` yield an **HTTP 403 Forbidden** response.
    *   Missing/Invalid token: Yields an **HTTP 401 Unauthorized** response.
*   **Route Targets**: Applied to all `/api/**` paths. Swagger UI and static files are bypassed for initial page loads.

---

## 📁 Repository Folder Structure

```
Insurance Management/
│
├── src/
│   ├── main/
│   │   ├── java/com/insurance/management/
│   │   │   ├── config/              # Web MVC, Interceptor registrations
│   │   │   ├── controller/          # REST Controller endpoints
│   │   │   ├── dto/                 # Request & Response Data Transfer Objects
│   │   │   ├── entity/              # JPA Database entities & Enums
│   │   │   ├── exception/           # Custom exceptions & @RestControllerAdvice
│   │   │   ├── interceptor/         # Custom HandlerInterceptor class
│   │   │   ├── mapper/              # Object mapping logic
│   │   │   ├── repository/          # JpaRepository database interfaces
│   │   │   └── service/             # Business Logic interface & implementations
│   │   │
│   │   └── resources/
│   │       ├── application.properties  # Database credentials and Swagger UI config
│   │       └── static/                 # Frontend Vanilla HTML, CSS, and JS files
│   │
│   └── test/                           # Unit and Integration test classes
│
├── pom.xml                             # Project object model and dependencies
└── README.md                           # System installation & usage guide
```

---

## 🛠️ Requirements & Setup

### Prerequisites
*   **Java**: Version 17 or higher.
*   **Maven**: Version 3.8+ (or Maven wrapper).
*   **MySQL Server**: Running instance (defaulting to port 3306).

### 1. Database Creation
The Spring properties are configured with `createDatabaseIfNotExist=true`. Consequently, you do **not** need to manually create the schema in MySQL. Spring Boot will automatically initialize the database `insurance_db` and generate the tables (`customers`, `policies`, `leads`) on startup.

### 2. Configuration Settings
To configure your database credentials, modify `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=password
```

### 3. Build the Application
Compile the files and bundle the project using:
```bash
mvn clean package -DskipTests
```

### 4. Running the Server
Launch the Spring Boot embedded Tomcat web server:
```bash
mvn spring-boot:run
```
The server will bind and start listening on port `8080`.

---

## 🧪 Testing and API Usage Guide

### Frontend Console
Open your web browser and navigate to:
👉 **[http://localhost:8080/index.html](http://localhost:8080/index.html)**

Use the **Security Context** selector in the header to switch roles:
1.  **Admin Mode**: Perform all operations (Create, Edit, Delete, Search) across all tables.
2.  **Agent Mode**: Creating, updating, and viewing will succeed. Triggering **Delete** will show a dynamic warning showing **HTTP 403 Forbidden**.
3.  **No Token Mode**: Performing any data action blocks the client and shows a warning showing **HTTP 401 Unauthorized**.

### Swagger UI API Docs
For full documentation and executing REST requests interactively:
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

Make sure to pass `X-Auth-Token` inside request headers when triggering endpoints.
"# Insurance-Management-System" 
"# Insurance-Management-System" 
