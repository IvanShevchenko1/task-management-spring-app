# **FlowPilot**

## **Overview**

This project is a Task Management System API built with Spring Boot. It allows users to create and manage projects, tasks, comments, labels, and file attachments in a structured way.

The application uses JWT-based authentication, role-based access control, and follows a layered architecture (Controller → Service → Repository). It integrates with MySQL for persistence, Liquibase for database migrations, and Dropbox for file storage.

## **Objectives** 

* Build a scalable and maintainable task management API
* Provide a structured way to manage projects and tasks
* Ensure secure access using JWT-based authentication and role-based authorization
* Support collaboration through comments and labels
* Integrate external storage (Dropbox) for handling attachments
* Enable reliable deployment using Docker

## **Functional Capabilities** 

* User registration and authentication (JWT)
* Create, update, and delete projects
* Manage tasks within projects (CRUD operations)
* Add, view, and delete comments on tasks
* Create and assign labels to tasks
* Upload and manage task attachments (via Dropbox)
* Role-based access control (USER / ADMIN)
* API documentation via Swagger UI
* Integration and unit testing support

## **Technology Stack**

Backend:
* Java 17
* Spring Boot
* Spring Security (JWT)
* Spring Data JPA / Hibernate

Database:
* MySQL
* Liquibase

Tools & Libraries:
* MapStruct
* Lombok
* Swagger / OpenAPI

Testing:
* JUnit 5
* Mockito
* Testcontainers

DevOps & Deployment:
* Docker
* Docker Compose

External Integration:
* Dropbox API (file storage)

## Getting Started

### Clone the repository

```bash
git clone https://github.com/IvanShevchenko1/task-management-spring-app.git
cd task-management-spring-app
```

### Running the Application

### Using Docker (recommended)

```bash
cp .env.template .env
./mvnw clean package
docker compose up --build
```

Application will be available at:

```
http://localhost:<SPRING_LOCAL_PORT>
```

## Environment Variables

### Database (MySQL)

| Variable               | Description                         | Example                |
|----------------------|-------------------------------------|------------------------|
| MYSQLDB_DATABASE     | Name of the application database     | task_management_db     |
| MYSQLDB_USER         | MySQL user                          | task_user              |
| MYSQLDB_PASSWORD     | MySQL user password                 | task_password          |
| MYSQL_ROOT_PASSWORD  | MySQL root password                 | root_password          |
| MYSQLDB_LOCAL_PORT   | Port exposed on host machine        | 3307                   |
| MYSQLDB_DOCKER_PORT  | MySQL port inside container         | 3306                   |

### Application

| Variable            | Description                         | Example |
|--------------------|-------------------------------------|--------|
| SPRING_LOCAL_PORT  | Port exposed on host                | 8080   |
| SPRING_DOCKER_PORT | Internal container port             | 8080   |

### Security (JWT)

| Variable        | Description                          | Example              |
|----------------|--------------------------------------|----------------------|
| JWT_SECRET     | Secret key for signing JWT tokens    | my_super_secret_key  |
| JWT_EXPIRATION | Token expiration time (ms)           | 3600000              |

### Dropbox Integration

| Variable               | Description                            |
|-----------------------|-----------------------------------------|
| DROPBOX_ACCESS_TOKEN  | Short-lived access token                |
| DROPBOX_REFRESH_TOKEN | Refresh token for long-term access      |
| DROPBOX_CLIENT_ID     | Dropbox app client ID                   |
| DROPBOX_CLIENT_SECRET | Dropbox app client secret               |

## **API Overview**

The Task Management API follows RESTful principles and provides endpoints for managing users, projects, tasks, comments, labels, and attachments.

### Authentication

| Method | Endpoint             | Description              |
|--------|----------------------|--------------------------|
| POST   | /auth/registration   | Register a new user      |
| POST   | /auth/login          | Authenticate and get JWT |

### Users

| Method | Endpoint         | Description                     |
|--------|------------------|---------------------------------|
| GET    | /users/me        | Get current user profile        |
| PUT    | /users/me        | Update current user             |
| PUT    | /users/{id}/role | Update user role (ADMIN only)   |

### Projects

| Method | Endpoint        | Description               |
|--------|-----------------|---------------------------|
| POST   | /projects       | Create a project          |
| GET    | /projects       | Get all user projects     |
| GET    | /projects/{id}  | Get project by id         |
| PUT    | /projects/{id}  | Update project            |
| DELETE | /projects/{id}  | Delete project            |

### Tasks

| Method | Endpoint                                   | Description                    |
|--------|--------------------------------------------|--------------------------------|
| POST   | /projects/{projectId}/tasks                | Create task in project         |
| GET    | /projects/{projectId}/tasks                | Get all tasks for project      |
| GET    | /projects/{projectId}/tasks/{taskId}       | Get task details               |
| PUT    | /projects/{projectId}/tasks/{taskId}       | Update task                    |
| DELETE | /projects/{projectId}/tasks/{taskId}       | Delete task                    |

### Comments

| Method | Endpoint                          | Description                    |
|--------|-----------------------------------|--------------------------------|
| POST   | /comments                         | Add comment to task            |
| GET    | /comments?taskId={taskId}         | Get comments for task          |
| DELETE | /comments/{id}                    | Delete comment                 |

### Labels

| Method | Endpoint        | Description                  |
|--------|-----------------|------------------------------|
| POST   | /labels         | Create label                 |
| GET    | /labels         | Get user labels              |
| PUT    | /labels/{id}    | Update label                 |
| DELETE | /labels/{id}    | Delete label                 |

### Attachments

| Method | Endpoint                               | Description                         |
|--------|----------------------------------------|-------------------------------------|
| POST   | /api/attachments                       | Upload attachment to task           |
| GET    | /api/attachments?taskId={taskId}       | Get attachments for task            |
| DELETE | /api/attachments/{id}                  | Delete attachment                   |

## **Swagger Documentation**

The project includes integrated Swagger (OpenAPI) documentation, which provides an interactive interface for exploring and testing all available API endpoints.

After starting the application, Swagger UI can be accessed at:

http://localhost:8080/swagger-ui/index.html

Swagger allows you to:

* View all available endpoints and their descriptions
* Inspect request and response models (DTOs)
* Execute API calls directly from the browser
* Authenticate using a JWT token via the "Authorize" button

## ER Diagram

<img width="1384" height="3216" alt="task_management_app@localhost" src="https://github.com/user-attachments/assets/088fb239-ab88-4d23-a16f-2d6a0b4c27b6" />

