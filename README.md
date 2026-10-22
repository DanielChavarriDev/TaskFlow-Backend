# TaskFlow Backend

A RESTful backend for **TaskFlow**, a project management and task tracking application built with Spring Boot and PostgreSQL.

The application provides a structured API for managing projects and their associated tasks, including task priorities, statuses, due dates, and project-based filtering.

## Overview

TaskFlow Backend is designed using a layered architecture that separates business rules, application use cases, persistence, and HTTP interfaces. This separation improves maintainability, testability, and extensibility.

## Features

### Project Management
- Create, retrieve, update, and delete projects.
- Retrieve projects by their unique identifiers.

### Task Management
- Create, retrieve, update, and delete tasks.
- Associate tasks with existing projects.
- Filter tasks by project.
- Update task statuses independently.
- Assign task priorities and due dates.
- Track task creation timestamps.
- Validate project references when creating tasks.

### Engineering Practices
- Layered architecture with separation of concerns.
- Domain-driven business logic.
- PostgreSQL persistence.
- Database versioning and migrations with Flyway.
- Automated unit and application context tests.
- REST API documentation with OpenAPI and Swagger UI.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 25 | Programming language |
| Spring Boot 4 | Backend framework |
| Maven | Build and dependency management |
| PostgreSQL 17 | Relational database |
| Spring Data JPA | Persistence abstraction |
| Hibernate | ORM |
| Flyway | Database migrations |
| Springdoc OpenAPI | API documentation |
| JUnit and Mockito | Automated testing |

## Architecture

The application follows a layered architecture:

```text
src/main/java/com/danielchavarri/taskflow/
├── project/
│   ├── domain/
│   ├── application/
│   ├── infrastructure/
│   └── interfaces/
├── task/
│   ├── domain/
│   ├── application/
│   ├── infrastructure/
│   └── interfaces/
└── TaskflowApplication.java
```

**Layer responsibilities:**

- **Domain:** Business entities, value types, and domain rules.
- **Application:** Use cases and orchestration of business operations.
- **Infrastructure:** Database persistence, JPA entities, repositories, and mapping.
- **Interfaces:** REST controllers, request and response DTOs, and exception handling.

This structure helps isolate business logic from technical implementation details.

## API Endpoints

Base URL: `http://localhost:8080`

### Projects

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/projects` | Create a project |
| `GET` | `/api/projects` | Retrieve all projects |
| `GET` | `/api/projects/{id}` | Retrieve a project by ID |
| `PUT` | `/api/projects/{id}` | Update a project |
| `DELETE` | `/api/projects/{id}` | Delete a project |

### Tasks

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/tasks` | Create a task |
| `GET` | `/api/tasks` | Retrieve all tasks |
| `GET` | `/api/tasks/{id}` | Retrieve a task by ID |
| `GET` | `/api/tasks/project/{projectId}` | Retrieve tasks by project |
| `PUT` | `/api/tasks/{id}` | Update task details |
| `PATCH` | `/api/tasks/{id}/status` | Change task status |
| `DELETE` | `/api/tasks/{id}` | Delete a task |

### Task Statuses

- `PENDING`
- `IN_PROGRESS`
- `COMPLETED`

### Task Priorities

- `LOW`
- `MEDIUM`
- `HIGH`

## Prerequisites

Before running the application, ensure you have installed:

- Java Development Kit (JDK) 25.
- PostgreSQL 17.
- Git.

Maven can be invoked through the Maven Wrapper included in the repository.

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/DanielChavarriDev/TaskFlow-Backend.git
cd TaskFlow-Backend
```

### 2. Create the PostgreSQL database

Connect to PostgreSQL and execute:

```sql
CREATE DATABASE taskflow_db;
```

The application uses Flyway to manage database schema migrations at startup.

### 3. Configure environment variables

Configure the following environment variables before running the application:

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | PostgreSQL JDBC connection URL | `jdbc:postgresql://localhost:5432/taskflow_db` |
| `DB_USERNAME` | PostgreSQL username | Not specified here |
| `DB_PASSWORD` | PostgreSQL password | Not specified here |

Set `DB_USERNAME` and `DB_PASSWORD` to match your local PostgreSQL configuration.

**Windows Command Prompt:**

```cmd
set DB_URL=jdbc:postgresql://localhost:5432/taskflow_db
set DB_USERNAME=your_postgres_username
set DB_PASSWORD=your_postgres_password
```

Replace the example credentials with your own. These commands configure the variables for the current terminal session.

### 4. Run the application

On Windows:

```cmd
mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

Once the application starts, the API will be available at:

`http://localhost:8080`

### 5. Access Swagger UI

Explore and test the REST endpoints through the interactive API documentation:

**[Open Swagger UI](http://localhost:8080/swagger-ui/index.html)**

The application must be running locally to access this address.

## Database Migrations

Flyway manages schema changes through versioned SQL migration scripts.

Current migrations:

- `V1__create_projects_table.sql`
- `V2__create_tasks_table.sql`

Migrations are applied automatically during application startup, keeping the database schema versioned and reproducible.

## Testing

Run the automated test suite with:

```cmd
mvnw.cmd test
```

The project includes tests for domain behavior, application services, error scenarios, and application context initialization.

The current verified test run completed with **27 tests passed and no failures**.

## Error Handling

The API includes centralized exception handling for relevant task operations.

Examples include:

- `404 Not Found` when a requested task does not exist.
- `400 Bad Request` for invalid application requests, including references to nonexistent projects during task creation.

## Project Status

The backend currently implements project and task management, PostgreSQL persistence, database migrations, REST API documentation, and automated tests.

Deployment and additional features can be introduced as the application evolves.

## Author

**Daniel Chávarri**

Software Engineering Student | Backend Development

GitHub: [@DanielChavarriDev](https://github.com/DanielChavarriDev)

---

*TaskFlow Backend — A project focused on maintainable REST API design, layered architecture, and reliable data persistence.*
