![Build Status](https://github.com/ChinthaRithwik/pms-backend/actions/workflows/ci.yml/badge.svg)
# 🗂️ Project Management System — Backend

A RESTful backend API built with **Spring Boot 3** for managing projects, tasks, and users with role-based access control and JWT authentication.

---

## 🛠️ Tech Stack

| Layer        | Technology                          |
|-------------|--------------------------------------|
| Language     | Java 17                             |
| Framework    | Spring Boot 3.5                     |
| Security     | Spring Security + JWT (jjwt 0.11.5) |
| Database     | MySQL                               |
| ORM          | Spring Data JPA / Hibernate         |
| Mapping      | ModelMapper 3.2                     |
| Validation   | Spring Boot Validation              |
| API Docs     | SpringDoc OpenAPI (Swagger UI)      |
| Build Tool   | Maven                               |
| Utilities    | Lombok                              |

---

## 📁 Project Structure

```
src/
└── main/
    └── java/com/example/ProjectManagementSystem/
        ├── config/             # CORS, Security, Swagger, ModelMapper configs
        ├── controller/         # REST controllers (Auth, Project, Task, User)
        ├── dto/                # Request/Response DTOs
        │   ├── projectDtos/
        │   ├── taskDtos/
        │   └── userDtos/
        ├── entity/             # JPA Entities (User, Project, Task, BaseEntity)
        │   └── enums/          # Role, StatusTypes, TaskStatus
        ├── exception/          # Custom exceptions + GlobalExceptionHandler
        ├── helper/             # AllowedTransitions (status state machine)
        ├── repository/         # Spring Data JPA Repositories
        ├── security/           # JwtService, JwtAuthenticationFilter, SecurityUtils
        ├── service/            # Service interfaces
        │   └── impl/           # Service implementations
        └── specification/      # TaskSpecification (dynamic filtering)
```

---

## ⚙️ Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8+

---

## 🚀 Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/YOUR_USERNAME/pms-backend.git
cd pms-backend
```

### 2. Create the MySQL database
```sql
CREATE DATABASE projectmanagement;
```

### 3. Configure environment variables

Create a `.env` file in the root (never commit this):
```
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_super_secret_jwt_key_at_least_32_chars
```

Or set them directly in your system/IDE environment.

### 4. Update `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/projectmanagement
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}
jwt.SecretKey=${JWT_SECRET}
spring.jpa.hibernate.ddl-auto=update
```

### 5. Run the application
```bash
./mvnw spring-boot:run
```

The server starts at **http://localhost:8080**

---

## 📖 API Documentation

Once running, visit Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

All endpoints require a **Bearer JWT token** (except `/api/v1/auth/**`).

---

## 🔐 Authentication

### Register
```
POST /api/v1/auth/signup
```
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

### Login
```
POST /api/v1/auth/login
```
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```
Returns a JWT token. Include it in all subsequent requests:
```
Authorization: Bearer <token>
```

---

## 📌 API Endpoints

### Projects
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/v1/projects` | Any user | Get all projects (paginated) |
| POST | `/api/v1/projects` | Any user | Create a project |
| GET | `/api/v1/projects/{id}` | Any user | Get project by ID |
| PATCH | `/api/v1/projects/{id}` | Any user | Update project details |
| PATCH | `/api/v1/projects/{id}/status` | Any user | Update project status |
| DELETE | `/api/v1/projects/{id}` | Any user | Delete project |

### Tasks
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/v1/tasks/project/{projectId}` | Any user | Create task in a project |
| GET | `/api/v1/tasks/project/{projectId}` | Any user | Get tasks for a project |
| GET | `/api/v1/tasks/{taskId}` | Any user | Get task by ID |
| GET | `/api/v1/tasks/overdue` | Any user | Get all overdue tasks |
| PATCH | `/api/v1/tasks/{taskId}` | Any user | Update task |
| PATCH | `/api/v1/tasks/{taskId}/status` | Any user | Update task status |
| PATCH | `/api/v1/tasks/{taskId}/assign` | **ADMIN** | Reassign task to user |
| DELETE | `/api/v1/tasks/{taskId}` | Any user | Delete task |
| GET | `/api/v1/tasks/search` | Any user | Search tasks (multi-filter) |

### Users (Admin only)
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/v1/users` | **ADMIN** | List all users (paginated) |
| POST | `/api/v1/users` | **ADMIN** | Create a user directly |
| GET | `/api/v1/users/{id}` | **ADMIN** | Get user by ID |
| GET | `/api/v1/users/{id}/tasks` | **ADMIN** | Get tasks assigned to user |
| GET | `/api/v1/users/{id}/projects` | **ADMIN** | Get projects of user |
| DELETE | `/api/v1/users/{id}` | **ADMIN** | Delete user |

---

## 🔄 Status Transitions

### Project Status
`PLANNING` → `IN_PROGRESS` → `COMPLETED` / `ON_HOLD`

### Task Status
`TODO` → `IN_PROGRESS` → `DONE`

Invalid transitions are rejected with a `400 Bad Request`.

---

## 👤 Roles

| Role | Description |
|------|-------------|
| `USER` | Default role. Can manage projects and tasks. |
| `ADMIN` | Full access including user management and task reassignment. |

---

## 🌍 Environment Variables

| Variable | Description |
|----------|-------------|
| `DB_PASSWORD` | MySQL database password |
| `JWT_SECRET` | Secret key for signing JWTs (min 32 chars) |

> ⚠️ Never commit real credentials to version control. Add `.env` to `.gitignore`.
