# 🚆 RailSarathi – Project Context & Technical Overview

**Document Location:** `RailSarathi/docs/context.md`  
**Project Name:** RailSarathi (Full-Stack Railway Reservation & Intelligent Fleet System)  
**Author:** Utsab Ghoshal  
**Active Branch:** `feature/user-repository`  
**Repository Architecture:** Production-Grade Decoupled Full-Stack Architecture (React 19 + Spring Boot 4.1 + PostgreSQL + Caffeine/Redis + TinyFish AI)

---

## 📌 Executive Summary

**RailSarathi** is an enterprise-grade railway reservation platform designed for Indian Railways passengers and administrators. It features high-speed train search with multi-stop intermediate calculation, live web status tracking via TinyFish AI, sub-millisecond multi-tier caching, session-isolated JWT authentication, a modern Dark Glassmorphism user experience, a **Universal Boilerplate Notification Service**, and a **Pluggable Payment Gateway & Service Boilerplate** with instant refund ledgers.

### Current System Status:
* **All Documentation Organized in `docs/`:** SRS, Architecture, Database Schema, REST API Specs, Frontend DDS, Deployment, Testing, Notification Boilerplate Guide, Payment Boilerplate Guide, and Context reports.
* **Backend Module (`backend/`):** **100% operational with 25/25 JUnit 5 and MockMvc integration tests passing.**
* **Frontend Module (`frontend/`):** **Fully Dynamic & Production-Ready** (React 19 + TypeScript + Vite). Zero mock dependencies; fully wired to backend REST endpoints (`/api/v1/auth`, `/api/v1/stations`, `/api/v1/trains`, `/api/v1/notifications`, `/api/v1/payments`, `/api/v1/users`).
* **Security:** All API keys, passwords, and tokens are protected in gitignored `.env` with strict zero-secret repository hygiene.

---

## 🏗️ Full-Stack System Architecture

```
                                +---------------------------------------------------+
                                |            React 19 Frontend Client               |
                                |       (TypeScript • Vite • Dark Glassmorphism)    |
                                +-------------------------+-------------------------+
                                                          |
                                           REST API (JSON / Bearer JWT / SSE Stream)
                                                          |
                                +-------------------------v-------------------------+
                                |             Spring Boot 4.1 Backend               |
                                |---------------------------------------------------|
                                | • AuthController: /auth/register, /login          |
                                | • StationController: /stations, /search           |
                                | • TrainController: /trains/search, /schedule,     |
                                |                    /live-status                   |
                                | • NotificationController: /notifications, /stream |
                                | • PaymentController: /payments/create-order,      |
                                |                      /verify, /refund, /history   |
                                | • UserController: /users/profile                  |
                                | • Security: JJWT 0.12.6, Session UUID, RBAC       |
                                | • Payments: Pluggable Providers (Sandbox,         |
                                |             Razorpay, Stripe) + Idempotency       |
                                | • Notifications: Pluggable Senders (In-App, Email,|
                                |                  SMS, Webhook) + SSE Streaming    |
                                | • Caching: Caffeine (In-Memory) / Redis Docker    |
                                | • Seeder: 16 Stations, 6 Flagship Trains, Seats   |
                                +-------------------+-------------------------------+
                                                    |
                                      +-------------+-------------+
                                      |                           |
                                      ▼                           ▼
                        +---------------------------+   +---------------------------+
                        |    PostgreSQL Database    |   |    TinyFish AI Platform   |
                        | (Stations, Trains, Routes,|   |  (Live Web Track Scraper) |
                        |  Payments, Refunds, Seats)|   |                           |
                        +---------------------------+   +---------------------------+
```

---

## 📁 Modular Directory Structure

```text
RailSarathi/
├── .env                       # Local active credentials (gitignored)
├── .env.example               # Safe environment blueprint
├── .gitignore                 # Root gitignore
├── docker-compose.yml         # Containerized Redis (6379) & PostgreSQL (5432)
├── README.md                  # Repository entrypoint
├── LICENSE                    # MIT License
│
├── docs/                      # 📚 Complete Documentation Suite
│   ├── context.md             # This document
│   ├── payment-boilerplate.md      # 💳 Universal Payment Gateway & Service Reusable Blueprint
│   ├── notification-boilerplate.md # 📦 Universal Notification Service Reusable Blueprint
│   ├── frontend-design.md     # Frontend Design Document Specification (DDS v1.0)
│   ├── requirements.md        # Software Requirements Specification (SRS v1.0)
│   ├── architecture.md        # Architecture & Component Topology
│   ├── database.md            # Relational Schema Design & Entity Mappings
│   ├── api.md                 # REST API Contracts & JSON Envelopes
│   ├── roadmap.md             # 10-Phase Milestone & Feature Delivery Roadmap
│   ├── deployment.md          # Local, Docker & Production Deployment
│   └── testing.md             # QA & Automated Testing Strategy
│
├── backend/                   # ☕ Spring Boot 4.1 Backend Module (Java 25)
│   ├── src/main/java/com/railsarathi/
│   │   ├── config/            # SecurityConfig, CacheConfig
│   │   ├── controller/        # Auth, Station, Train, Notification, Payment, User Controllers
│   │   ├── dto/               # ApiResponse, AuthResponse, PaymentOrderDto, VerifyPaymentRequest, NotificationDto...
│   │   ├── entity/            # User, Station, Train, TrainSchedule, Coach, Seat, Notification, PaymentTransaction, RefundTransaction
│   │   ├── enums/             # Role, TrainType, CoachClass, BerthType, SeatStatus, PaymentStatus, PaymentMethod, PaymentGatewayType...
│   │   ├── event/             # NotificationEvent (Spring ApplicationEvent)
│   │   ├── exception/         # GlobalExceptionHandler
│   │   ├── repository/        # UserRepository, StationRepository, TrainRepository, PaymentTransactionRepository, RefundTransactionRepository...
│   │   ├── security/          # JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService
│   │   ├── seeder/            # DatabaseSeeder (16 Stations, 6 Trains, Initial Notifications)
│   │   └── service/           # Auth, Station, TrainSearch, TinyFishScraper, Notification, Payment Services
│   │       ├── notification/  # SseConnectionManager, InApp, Email, Sms, Webhook Senders
│   │       └── payment/       # PaymentGatewayProvider, Factory, MockSandbox, Razorpay, Stripe Providers
│   └── src/test/              # 25 Integration Tests (100% Pass Rate)
│
└── frontend/                  # ⚛️ React 19 Frontend Module (TypeScript + Vite)
    ├── src/
    │   ├── api/               # client.ts, authApi.ts, stationApi.ts, trainApi.ts, notificationApi.ts, paymentApi.ts
    │   ├── types/             # api, auth, station, train, booking, notification, payment types
    │   ├── hooks/             # useNotifications.ts (SSE Stream), usePayment.ts (Checkout Lifecycle)
    │   ├── context/           # AuthContext.tsx (JWT storage, profile sync, session UUID)
    │   ├── components/
    │   │   ├── common/        # Navbar.tsx (with NotificationBell), Footer.tsx, LoadingSpinner.tsx, ErrorAlert.tsx
    │   │   ├── auth/          # AuthModal.tsx (Live Login/Register modal with validation)
    │   │   ├── notifications/ # NotificationBell.tsx (Dropdown drawer), NotificationToast.tsx (Floating alert)
    │   │   ├── payment/       # PaymentModal.tsx (Multi-instrument checkout + Sandbox), PaymentReceiptModal.tsx
    │   │   ├── search/        # StationInput.tsx (Debounced autocomplete), SearchConsole.tsx
    │   │   ├── trains/        # TrainCard.tsx, LiveStatusBadge.tsx (TinyFish tracker), ScheduleTable.tsx
    │   │   ├── booking/       # PassengerForm.tsx (Berth matrix), FareSummary.tsx
    │   │   └── ticket/        # BoardingPass.tsx (Printable ticket with QR & PNR)
    │   ├── pages/             # HomePage, SearchResultsPage, TrainDetailsPage, BookingPage, ConfirmationPage, DashboardPage, AdminPage
    │   ├── App.tsx            # Full application router & Shell
    │   ├── main.tsx           # React root mount
    │   └── styles.css         # Deep slate glassmorphism design system
    ├── index.html             # Responsive HTML5 entry point
    └── vite.config.ts         # Vite server with /api proxy forwarding
```

---

## 🚦 Milestone Progress Tracker

| Milestone | Description | Status | Verification |
| :---: | :--- | :---: | :--- |
| **Phase 1** | System Documentation & Frontend DDS | ✅ Completed | 11 specs consolidated in `docs/` |
| **Phase 2** | JWT Security, Session UUID & Auth REST API | ✅ Completed | 6 integration tests passing |
| **Phase 3** | Indian Railways Fleet Backbone & Seeder | ✅ Completed | 16 Stations, 6 Trains pre-seeded |
| **Phase 4** | Route Search & Dynamic Distance Pricing Engine | ✅ Completed | Multi-stop matching verified |
| **Phase 5** | TinyFish AI Live Train Scraper Integration | ✅ Completed | Real-time web tracking & fallback |
| **Phase 6** | Zero-Setup Multi-Tier Caching Layer | ✅ Completed | 3m live status, 30m schedules, 60m stations |
| **Phase 7** | Dynamic React 19 + TypeScript Frontend Base | ✅ Completed | 100% dynamic, fully wired to REST APIs |
| **Phase 8** | Universal Reusable Notification Boilerplate | ✅ Completed | SSE Streaming + NotificationBell UI |
| **Phase 9** | Universal Reusable Payment Gateway & Service | ✅ Completed | **25/25 Backend Tests passing + PaymentModal UI** |
| **Phase 10** | Custom Ticketing Concurrency & Release Prep | ⏳ Next | Seat-locking concurrency & distribution pack |
