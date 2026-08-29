# 🚆 RailSarathi – Project Context & Progress Report

**Document Date:** August 2026  
**Project Name:** RailSarathi (Full-Stack Railway Reservation System)  
**Author:** Utsab Ghoshal  
**Repository Branch:** `feature/user-repository` (Active) | Base: `main` (`v0.1.0` Tagged)  
**Repository Architecture:** Decoupled Full-Stack Web Application (React + Spring Boot + PostgreSQL)  

---

## 📌 Executive Summary

**RailSarathi** is an enterprise-inspired, full-stack railway reservation platform designed to support passenger booking workflows, train scheduling, seat inventory management, and administrative railway operations. The project is structured and developed following production-grade software engineering best practices, encompassing comprehensive technical documentation, a clean 3-tier layered architecture, domain-driven schema design, and modular backend/frontend services.

As of today:
* **Phase 1 (Planning & Architecture):** Fully documented (SRS, Architecture, Database, API, Deployment, Testing, Frontend DDS).
* **Phase 2 & 3 (Backend Security & Session-Aware Auth):** Completed & Verified.
* **Phase 4 & 5 (Railway Domain Backbone, Seed Data & TinyFish AI Live Scraper):** Completed & Verified with 16 major Indian railway hubs, 6 authentic flagship trains, intermediate multi-stop route search, and TinyFish AI live tracking integration.
* **Multi-Tier Caching Layer (Zero-Hassle Caffeine In-Memory + Optional Docker Redis):** **Fully Completed & Verified** with 3-minute live tracking cache, 30-minute timetable cache, and 60-minute station cache. All 15 backend integration tests are passing (100% success rate).

---

## 🏗️ System Architecture & Technology Stack

```
                                +---------------------------------------------------+
                                |                 React Frontend                    |
                                |     (TypeScript • Tailwind CSS • React Router)    |
                                +-------------------------+-------------------------+
                                                          |
                                                    REST API / HTTPS
                                                          |
                                +-------------------------v-------------------------+
                                |               Spring Boot Backend                 |
                                |---------------------------------------------------|
                                | • Controllers: Auth, Users, Stations, Trains      |
                                | • Services: AuthService, UserService,             |
                                |             TrainSearchService, TinyFishScraper   |
                                | • Caching: Caffeine In-Memory (Zero-Setup) /      |
                                |            Redis (Docker Compose)                 |
                                | • Security: JWT (24h) + Session Token (UUID)      |
                                | • Seeder: 16 Stations, 6 Flagship Trains, Coaches |
                                | • Persistence: Spring Data JPA + PostgreSQL / H2  |
                                +-------------------+-------------------------------+
                                                    |
                                      +-------------+-------------+
                                      |                           |
                                      ▼                           ▼
                        +---------------------------+   +---------------------------+
                        |    PostgreSQL Database    |   |    TinyFish AI Platform   |
                        | (Stations, Trains, Routes)|   |  (Live Web Track Scraper) |
                        +---------------------------+   +---------------------------+
```

### Technology Breakdown

| Tier | Technologies / Frameworks | Status |
| :--- | :--- | :--- |
| **Backend** | Java 25, Spring Boot 4.1.0, Spring Security 6, Spring Data JPA, Hibernate, Maven | ✅ Active & Verified |
| **Caching Layer** | Spring Cache, Caffeine In-Memory Cache (0-setup), Redis Docker Compose | ✅ Completed & Verified |
| **Database** | PostgreSQL (`railsarathi`), JDBC Driver, HikariCP, H2 (test-isolated) | ✅ Configured & Seeded |
| **Authentication** | JJWT (0.12.6), BCrypt, Session Tracking (`sessionId` UUID), RBAC | ✅ Completed & Verified |
| **External AI Scraper** | TinyFish AI Search & Agent APIs (`sk-tinyfish-***`) | ✅ Completed & Verified |
| **Frontend** | React 19, TypeScript, Tailwind CSS, React Router, Vite | ⏳ Next Step (Frontend Base) |
| **Testing** | JUnit 5, Spring Security Test, MockMvc (15/15 automated tests passing) | ✅ Verified |

---

## 📁 Repository Structure & Organization

```text
Travel/
└── RailSarathi/
    ├── .dist/                      # Build distribution folder
    ├── .git/                       # Version control history & branches
    ├── .github/
    │   └── ISSUE_TEMPLATE/
    │       ├── issue_template.md   # Standardized GitHub issue reporting
    │       └── pull_request_template.md # Standardized PR template
    ├── .vscode/
    │   ├── launch.json             # VS Code debug & launch configuration
    │   └── settings.json           # IDE formatting and editor settings
    ├── backend/                    # Spring Boot backend application
    │   ├── .mvn/wrapper/           # Maven wrapper files
    │   ├── src/
    │   │   ├── main/
    │   │   │   ├── java/com/railsarathi/
    │   │   │   │   ├── config/
    │   │   │   │   │   ├── CacheConfig.java               # Caffeine cache manager & custom TTLs
    │   │   │   │   │   └── SecurityConfig.java            # Spring Security & CORS configuration
    │   │   │   │   ├── controller/
    │   │   │   │   │   ├── AuthController.java            # /api/v1/auth/register, /login
    │   │   │   │   │   ├── StationController.java         # /api/v1/stations, /search
    │   │   │   │   │   ├── TrainController.java           # /api/v1/trains/search, /schedule, /live-status
    │   │   │   │   │   └── UserController.java            # /api/v1/users/profile, /{id}
    │   │   │   │   ├── dto/
    │   │   │   │   │   ├── ApiResponse.java               # Standard JSON response envelope
    │   │   │   │   │   ├── AuthResponse.java              # JWT token + sessionId payload
    │   │   │   │   │   ├── ClassAvailabilityDto.java      # Fare & seat inventory matrix
    │   │   │   │   │   ├── LiveTrainStatusDto.java        # TinyFish AI live status model
    │   │   │   │   │   ├── LoginRequest.java              # Login credentials DTO
    │   │   │   │   │   ├── RegisterRequest.java           # Registration with Jakarta validations
    │   │   │   │   │   ├── StationDto.java                # Station summary & autocomplete DTO
    │   │   │   │   │   ├── TrainScheduleDto.java          # Route timetable DTO
    │   │   │   │   │   ├── TrainSearchResultDto.java      # Comprehensive train search model
    │   │   │   │   │   └── UserProfileDto.java            # Safe user profile representation
    │   │   │   │   ├── entity/
    │   │   │   │   │   ├── Coach.java                     # Coach inventory (1A, 2A, 3A, CC, EC, SL)
    │   │   │   │   │   ├── Seat.java                      # Seat entity with berthType & status
    │   │   │   │   │   ├── Station.java                   # Railway station entity
    │   │   │   │   │   ├── Train.java                     # Train entity with route & runsOnDays
    │   │   │   │   │   ├── TrainSchedule.java             # Multi-stop intermediate timetable
    │   │   │   │   │   └── User.java                      # User entity with Role & activeSessionId
    │   │   │   │   ├── enums/
    │   │   │   │   │   ├── BerthType.java                 # LOWER, MIDDLE, UPPER, SIDE_LOWER...
    │   │   │   │   │   ├── CoachClass.java                # 1A, 2A, 3A, 3E, CC, EC, SL, 2S
    │   │   │   │   │   ├── Role.java                      # ROLE_PASSENGER, ROLE_ADMIN, ROLE_OPERATOR
    │   │   │   │   │   ├── SeatStatus.java                # AVAILABLE, BOOKED, BLOCKED, RAC
    │   │   │   │   │   └── TrainType.java                 # VANDE_BHARAT, RAJDHANI, SHATABDI...
    │   │   │   │   ├── exception/
    │   │   │   │   │   ├── BadRequestException.java
    │   │   │   │   │   ├── GlobalExceptionHandler.java    # Standardized @RestControllerAdvice
    │   │   │   │   │   ├── ResourceNotFoundException.java
    │   │   │   │   │   ├── UnauthorizedException.java
    │   │   │   │   │   └── UserAlreadyExistsException.java
    │   │   │   │   ├── repository/
    │   │   │   │   │   ├── CoachRepository.java
    │   │   │   │   │   ├── SeatRepository.java
    │   │   │   │   │   ├── StationRepository.java
    │   │   │   │   │   ├── TrainRepository.java
    │   │   │   │   │   ├── TrainScheduleRepository.java
    │   │   │   │   │   └── UserRepository.java
    │   │   │   │   ├── seeder/
    │   │   │   │   │   └── DatabaseSeeder.java            # Pre-seeds 16 stations & 6 flagship trains
    │   │   │   │   ├── security/
    │   │   │   │   │   ├── CustomUserDetails.java
    │   │   │   │   │   ├── CustomUserDetailsService.java
    │   │   │   │   │   ├── JwtAuthenticationEntryPoint.java
    │   │   │   │   │   ├── JwtAuthenticationFilter.java
    │   │   │   │   └── JwtTokenProvider.java
    │   │   │   │   ├── service/
    │   │   │   │   │   ├── impl/
    │   │   │   │   │   │   ├── AuthServiceImpl.java
    │   │   │   │   │   │   ├── StationServiceImpl.java
    │   │   │   │   │   │   ├── TinyFishScraperServiceImpl.java # TinyFish AI scraper with @Cacheable
    │   │   │   │   │   │   ├── TrainSearchServiceImpl.java     # Route search with schedule cache
    │   │   │   │   │   │   └── UserServiceImpl.java
    │   │   │   │   │   ├── AuthService.java
    │   │   │   │   │   ├── StationService.java
    │   │   │   │   │   ├── TinyFishScraperService.java
    │   │   │   │   │   ├── TrainSearchService.java
    │   │   │   │   │   └── UserService.java
    │   │   │   │   └── BackendApplication.java
    │   │   │   └── resources/
    │   │   │       └── application.properties             # DB, JWT, CORS, Cache & TinyFish AI config
    │   │   └── test/
    │   │       ├── java/com/railsarathi/
    │   │       │   ├── AuthIntegrationTests.java          # 6 auth & security test cases
    │   │       │   ├── BackendApplicationTests.java       # Context loader test
    │   │       │   ├── CacheIntegrationTests.java         # 2 caching & TTL verification tests
    │   │       │   ├── RailSarathiApplicationTests.java    # Persistence integration test
    │   │       │   └── TrainSearchAndScraperTests.java    # 5 station, search & scraper tests
    │   │       └── resources/
    │   │           └── application.properties             # Test config with in-memory cache & H2
    │   ├── mvnw & mvnw.cmd         # Maven executable wrappers
    │   ├── pom.xml                 # Maven dependencies & build configurations
    │   └── HELP.md
    ├── docker-compose.yml          # Optional Docker Redis (6379) & Postgres (5432)
    ├── docs/                       # Project Documentation Suite
    │   ├── frontend-design.md      # Frontend Design Document Specification (DDS v1.0)
    │   ├── requirements.md         # Software Requirements Specification (SRS v1.0)
    │   ├── architecture.md         # System Architecture & Component Interactions (v0.1)
    │   ├── database.md             # Schema Design, Entities & Relational Mappings (v0.1)
    │   ├── api.md                  # RESTful API Specifications & Response Envelopes (v0.1)
    │   ├── roadmap.md              # 10-Phase Milestone & Feature Delivery Roadmap
    │   ├── deployment.md           # Local, Staging & Cloud Deployment Strategies (v0.1)
    │   └── testing.md              # Multi-tier QA, Unit & Integration Testing Strategy (v0.1)
    ├── frontend/                   # Frontend workspace (reserved for React application)
    ├── LICENSE                     # MIT License
    └── README.md                   # Project overview, objectives, and guide
```

---

## 📊 Summary of Completed Work

### 1. Multi-Tier Zero-Hassle Caching Layer
* **Caffeine In-Memory Engine:** Operates directly inside Spring Boot with zero external daemon or password required.
* **Cache Expirations & Policies:**
  - `train_live_status`: **3 minutes (180s)** TTL. Prevents redundant external calls to TinyFish AI, returning in `<1ms`.
  - `train_schedules`: **30 minutes** TTL.
  - `stations`: **60 minutes** TTL.
* **Force Refresh:** `GET /api/v1/trains/{trainNumber}/live-status?forceRefresh=true` allows bypassing cache when explicit real-time re-fetch is desired.
* **Docker Compose:** Provided `docker-compose.yml` for optional Redis & PostgreSQL containers.

### 2. Authentic Indian Railways Fleet & Route Search
* **16 Stations & 6 Flagship Trains:** `22301` Vande Bharat, `12301` Rajdhani, `12951` Tejas Rajdhani, `12002` Shatabdi, `12245` Duronto, `12626` Kerala Exp.
* **Multi-stop search engine:** Calculates accurate travel distances, departure/arrival times, and dynamic class fares.

### 3. TinyFish AI Live Scraper
* Live train running status queries with AI web insights and automatic timetable fallback.

---

## 🚦 Milestone Progress Tracker

| Phase | Milestone Description | Status | Details |
| :---: | :--- | :---: | :--- |
| **Phase 1** | Project Planning & Documentation | ✅ Completed | SRS, Architecture, DB design, API, Frontend DDS finalized |
| **Phase 2 & 3** | Backend Security & Auth Base | ✅ Completed | JWT (24h), BCrypt, Session Tracking, Auth & User APIs |
| **Phase 4 & 5** | Railway Domain, Seeder & TinyFish AI | ✅ Completed | 16 Stations, 6 Trains, Search Engine, TinyFish live scraper |
| **Phase 6** | Zero-Hassle Caching Layer | ✅ Completed | In-Memory Caffeine + Docker Redis Compose, 15/15 tests passing |
| **Phase 7** | Frontend Client Base Setup | ⏳ Next Step | React + TypeScript + Tailwind CSS UI shell, search widget, & modals |
| **Phase 8** | Full-Stack Integration | ⏳ Planned | Connect React UI to Spring Boot Auth, Search & Live Tracking APIs |
| **Phase 9** | Custom Ticketing & Fast Refund Engine | ⏳ Planned | Seat locking, atomic reservation, dynamic refund calculation rules |
