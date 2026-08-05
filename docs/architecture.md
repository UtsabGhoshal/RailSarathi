# 🏗️ System Architecture

## Overview

RailSarathi follows a modern **three-tier architecture**, separating the presentation layer, business logic, and data layer. This architecture improves maintainability, scalability, security, and testability.

The application is designed as a RESTful web application where the frontend communicates with the backend through secure HTTP APIs.

---

# Architecture Overview

```text
+----------------------------------------------------+
|                React Frontend                      |
|  (TypeScript • Tailwind CSS • React Router)        |
+-------------------------+--------------------------+
                          |
                    HTTPS / REST API
                          |
+-------------------------v--------------------------+
|               Spring Boot Backend                  |
|----------------------------------------------------|
| Controllers                                        |
| Services                                           |
| Security (JWT + Spring Security)                   |
| Repositories (Spring Data JPA)                     |
+-------------------------+--------------------------+
                          |
                    Hibernate / JPA
                          |
+-------------------------v--------------------------+
|                  PostgreSQL Database               |
+----------------------------------------------------+
```

---

# Architecture Layers

## 1. Presentation Layer

Responsible for interacting with users.

### Responsibilities

* User Interface
* Form Validation
* Route Navigation
* Calling REST APIs
* Displaying Data

### Technologies

* React
* TypeScript
* Tailwind CSS
* React Router

---

## 2. Business Layer

Implements the application's business rules.

### Responsibilities

* User Authentication
* Train Search
* Seat Allocation
* Ticket Booking
* Payment Processing
* Waiting List Management

### Technologies

* Spring Boot
* Spring Security
* Spring Data JPA

---

## 3. Data Layer

Responsible for storing and retrieving information.

### Responsibilities

* Data Persistence
* Database Transactions
* Query Optimization
* Entity Relationships

### Technology

* PostgreSQL

---

# Backend Architecture

The backend follows a layered architecture.

```text
Controller
      │
      ▼
Service
      │
      ▼
Repository
      │
      ▼
PostgreSQL
```

## Controller Layer

Handles incoming HTTP requests and returns responses.

Example:

```
GET /api/trains
POST /api/bookings
POST /api/auth/login
```

---

## Service Layer

Contains business logic.

Examples:

* Validate booking requests
* Allocate seats
* Calculate fares
* Generate tickets

---

## Repository Layer

Handles database operations using Spring Data JPA.

Examples:

* Save booking
* Find available trains
* Update seat status

---

# Authentication Flow

```text
User Login
      │
      ▼
Spring Security
      │
Validate Credentials
      │
Generate JWT
      │
Return Token
      │
Frontend stores token
      │
Authenticated API Requests
```

---

# Request Flow

```text
User clicks "Search Train"
        │
        ▼
React Frontend
        │
GET /api/trains
        │
Spring Boot Controller
        │
Service
        │
Repository
        │
PostgreSQL
        │
JSON Response
        │
React UI Updates
```

---

# Project Structure

```text
RailSarathi/

frontend/
backend/
docs/
screenshots/
README.md
LICENSE
```

---

# Design Principles

The project follows these software engineering principles:

* Separation of Concerns
* Layered Architecture
* Single Responsibility Principle
* Dependency Injection
* RESTful API Design
* Secure Authentication
* Modular Development

---

# Scalability

The architecture is designed to support future enhancements, including:

* Docker-based deployment
* CI/CD pipelines
* Cloud deployment
* Microservices migration
* Caching with Redis
* Real-time notifications
* Railway analytics

---

# Future Improvements

Future architectural enhancements may include:

* API Gateway
* Message Queue
* Redis Cache
* WebSocket Notifications
* Monitoring & Logging
* Distributed Services

---

# Version History

| Version | Description                 |
| ------- | --------------------------- |
| v0.1    | Initial system architecture |
