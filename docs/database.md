# 🗄️ Database Design

## Overview

RailSarathi uses **PostgreSQL** as its primary relational database management system.

The database is designed following normalization principles to reduce redundancy, maintain data integrity, and support scalable railway reservation operations.

The system models real-world railway entities such as users, trains, stations, routes, coaches, seats, bookings, and payments.

---

# Database Management System

* **Database:** PostgreSQL
* **ORM:** Spring Data JPA + Hibernate
* **Primary Key Strategy:** UUID (Preferred) / Auto Increment (Development)
* **Relationships:** One-to-One, One-to-Many, Many-to-One

---

# Database Design Principles

* Normalized schema (up to Third Normal Form where practical)
* Referential integrity using Foreign Keys
* ACID-compliant transactions
* Optimized indexing for frequently queried columns
* Soft deletion where appropriate
* Audit fields for important entities

---

# Core Entities

## User

Represents passengers and administrators.

| Column     | Type      |
| ---------- | --------- |
| id         | UUID      |
| full_name  | VARCHAR   |
| email      | VARCHAR   |
| password   | VARCHAR   |
| phone      | VARCHAR   |
| role       | ENUM      |
| created_at | TIMESTAMP |
| updated_at | TIMESTAMP |

---

## Station

Represents railway stations.

| Column       | Type    |
| ------------ | ------- |
| id           | UUID    |
| station_code | VARCHAR |
| station_name | VARCHAR |
| city         | VARCHAR |
| state        | VARCHAR |

---

## Train

Stores train information.

| Column       | Type    |
| ------------ | ------- |
| id           | UUID    |
| train_number | VARCHAR |
| train_name   | VARCHAR |
| train_type   | VARCHAR |

---

## Route

Represents the route followed by a train.

| Column                 | Type    |
| ---------------------- | ------- |
| id                     | UUID    |
| train_id               | UUID    |
| source_station_id      | UUID    |
| destination_station_id | UUID    |
| total_distance         | DECIMAL |

---

## Schedule

Defines departure and arrival timings.

| Column         | Type    |
| -------------- | ------- |
| id             | UUID    |
| train_id       | UUID    |
| station_id     | UUID    |
| arrival_time   | TIME    |
| departure_time | TIME    |
| day_number     | INTEGER |

---

## Coach

Represents coaches attached to a train.

| Column       | Type    |
| ------------ | ------- |
| id           | UUID    |
| train_id     | UUID    |
| coach_number | VARCHAR |
| coach_type   | VARCHAR |
| total_seats  | INTEGER |

---

## Seat

Represents seats inside a coach.

| Column      | Type    |
| ----------- | ------- |
| id          | UUID    |
| coach_id    | UUID    |
| seat_number | VARCHAR |
| berth_type  | VARCHAR |
| seat_status | ENUM    |

---

## Booking

Stores ticket bookings.

| Column         | Type      |
| -------------- | --------- |
| id             | UUID      |
| user_id        | UUID      |
| journey_date   | DATE      |
| booking_status | ENUM      |
| booking_time   | TIMESTAMP |
| total_fare     | DECIMAL   |

---

## Passenger

Passengers travelling under a booking.

| Column           | Type    |
| ---------------- | ------- |
| id               | UUID    |
| booking_id       | UUID    |
| full_name        | VARCHAR |
| age              | INTEGER |
| gender           | VARCHAR |
| berth_preference | VARCHAR |

---

## Ticket

Stores generated ticket information.

| Column        | Type      |
| ------------- | --------- |
| id            | UUID      |
| booking_id    | UUID      |
| pnr_number    | VARCHAR   |
| ticket_status | ENUM      |
| issued_at     | TIMESTAMP |

---

## Payment

Stores payment details.

| Column                | Type    |
| --------------------- | ------- |
| id                    | UUID    |
| booking_id            | UUID    |
| amount                | DECIMAL |
| payment_method        | VARCHAR |
| payment_status        | ENUM    |
| transaction_reference | VARCHAR |

---

# Entity Relationships

* One User can create many Bookings.
* One Train can have many Coaches.
* One Coach contains many Seats.
* One Train follows one Route.
* One Route consists of multiple Schedule entries.
* One Booking can contain multiple Passengers.
* One Booking generates one Ticket.
* One Booking has one Payment.

---

# Planned Entity Relationship Diagram

An ER Diagram will be added after the database schema has been finalized.

---

# Future Database Enhancements

The following entities may be introduced in future releases:

* Waiting List
* RAC Allocation
* Refund
* Notification
* Review
* Coupon
* Audit Log
* Fare Rules
* Train Delay History

---

# Design Considerations

* Prevent duplicate bookings using database transactions.
* Ensure referential integrity using Foreign Keys.
* Maintain booking consistency through ACID transactions.
* Optimize search queries using indexes.
* Support future scalability without major schema changes.

---

# Version History

| Version | Description             |
| ------- | ----------------------- |
| v0.1    | Initial database design |
