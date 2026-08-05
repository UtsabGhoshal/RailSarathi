# 🚀 Deployment Guide

## Overview

This document describes the deployment strategy for **RailSarathi**.

The application follows a client-server architecture, where the frontend, backend, and database are deployed independently.

The deployment process is designed to support local development, staging, and future production environments.

---

# Deployment Architecture

```text
                    Internet
                        │
                        ▼
                React Frontend
              (Vercel / Netlify)
                        │
                  HTTPS Requests
                        │
                        ▼
               Spring Boot Backend
             (Render / Railway / AWS)
                        │
                 Spring Data JPA
                        │
                        ▼
               PostgreSQL Database
```

---

# Technology Stack

## Frontend

* React
* TypeScript
* Tailwind CSS

Deployment Platform (Planned)

* Vercel

Alternative

* Netlify

---

## Backend

* Spring Boot
* Java 21
* Maven

Deployment Platform (Planned)

* Render

Alternative

* Railway
* AWS EC2
* Docker

---

## Database

* PostgreSQL

Development

* Local PostgreSQL

Production

* Neon PostgreSQL (Planned)

Alternative

* Railway PostgreSQL
* Supabase PostgreSQL
* AWS RDS

---

# Development Environment

## Requirements

* Java 21
* Node.js
* npm
* PostgreSQL
* Maven
* Git

---

# Local Deployment

## Clone Repository

```bash
git clone https://github.com/UtsabGhoshal/RailSarathi.git
```

---

## Backend

Navigate to backend

```bash
cd backend
```

Run application

```bash
./mvnw spring-boot:run
```

Backend URL

```text
http://localhost:8080
```

---

## Frontend

Navigate to frontend

```bash
cd frontend
```

Install dependencies

```bash
npm install
```

Start development server

```bash
npm run dev
```

Frontend URL

```text
http://localhost:5173
```

---

# Database Configuration

Configure PostgreSQL connection using environment variables.

Example

```properties
DB_HOST=
DB_PORT=
DB_NAME=
DB_USERNAME=
DB_PASSWORD=
```

Environment files should **never** be committed to GitHub.

---

# Environment Variables

Backend

```text
JWT_SECRET

JWT_EXPIRATION

DB_HOST

DB_PORT

DB_NAME

DB_USERNAME

DB_PASSWORD
```

Frontend

```text
VITE_API_BASE_URL
```

---

# Build Process

Frontend

```bash
npm run build
```

Backend

```bash
./mvnw clean package
```

Generated artifact

```text
target/
```

---

# CI/CD (Planned)

Future releases will use GitHub Actions to automate:

* Build
* Testing
* Deployment

Workflow

```text
Push

↓

GitHub Actions

↓

Run Tests

↓

Build

↓

Deploy

↓

Production
```

---

# Docker (Planned)

Future versions will support containerized deployment using Docker.

Planned containers

* Frontend
* Backend
* PostgreSQL

---

# Production Deployment

Planned Services

| Component | Platform        |
| --------- | --------------- |
| Frontend  | Vercel          |
| Backend   | Render          |
| Database  | Neon PostgreSQL |

---

# Security Considerations

* HTTPS Enabled
* Secure Environment Variables
* JWT Authentication
* Password Hashing
* CORS Configuration
* Database Credentials stored securely
* No secrets committed to GitHub

---

# Monitoring (Future)

Planned monitoring tools

* Spring Boot Actuator
* Grafana
* Prometheus
* Sentry

---

# Backup Strategy

Future versions will include

* Automated PostgreSQL Backups
* Database Recovery Procedures
* Disaster Recovery Documentation

---

# Deployment Checklist

Before every production release

* All tests pass
* Build successful
* Environment variables configured
* Database migration completed
* Documentation updated
* Release tagged in GitHub

---

# Version History

| Version | Description                 |
| ------- | --------------------------- |
| v0.1    | Initial deployment strategy |
