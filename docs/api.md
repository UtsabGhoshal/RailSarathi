# 🌐 API Documentation

## Overview

RailSarathi exposes a RESTful API that enables communication between the React frontend and the Spring Boot backend.

The API follows REST principles and exchanges data using JSON over HTTPS.

---

# Base URL

Development

```text
http://localhost:8080/api/v1
```

Production

```text
https://api.railsarathi.com/api/v1
```

---

# Authentication

RailSarathi uses **JWT (JSON Web Token)** for authentication.

Protected endpoints require the following HTTP header:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

# API Response Format

## Success Response

```json
{
  "success": true,
  "message": "Request completed successfully.",
  "data": {}
}
```

---

## Error Response

```json
{
  "success": false,
  "message": "Invalid request.",
  "errors": []
}
```

---

# Authentication APIs

## Register User

**POST**

```text
/api/v1/auth/register
```

### Request

```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "9876543210"
}
```

### Response

```json
{
  "success": true,
  "message": "User registered successfully."
}
```

---

## Login

**POST**

```text
/api/v1/auth/login
```

### Request

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

### Response

```json
{
  "token": "<JWT_TOKEN>"
}
```

---

# User APIs

## Get User Profile

**GET**

```text
/api/v1/users/profile
```

---

## Update Profile

**PUT**

```text
/api/v1/users/profile
```

---

# Station APIs

## Get All Stations

**GET**

```text
/api/v1/stations
```

---

## Get Station by ID

**GET**

```text
/api/v1/stations/{id}
```

---

## Create Station

**POST**

```text
/api/v1/stations
```

(Admin only)

---

# Train APIs

## Get All Trains

**GET**

```text
/api/v1/trains
```

---

## Search Trains

**GET**

```text
/api/v1/trains/search
```

### Query Parameters

```text
source

destination

journeyDate
```

Example

```text
/api/v1/trains/search?source=HWH&destination=NDLS&journeyDate=2026-10-12
```

---

## Get Train Details

**GET**

```text
/api/v1/trains/{id}
```

---

# Schedule APIs

## Get Train Schedule

**GET**

```text
/api/v1/schedules/{trainId}
```

---

# Booking APIs

## Check Seat Availability

**GET**

```text
/api/v1/bookings/availability
```

---

## Book Ticket

**POST**

```text
/api/v1/bookings
```

---

## Booking History

**GET**

```text
/api/v1/bookings/history
```

---

## Cancel Booking

**DELETE**

```text
/api/v1/bookings/{bookingId}
```

---

# Ticket APIs

## Download Ticket

**GET**

```text
/api/v1/tickets/{ticketId}
```

---

## View Ticket

**GET**

```text
/api/v1/tickets/{ticketId}
```

---

# Payment APIs

## Create Payment

**POST**

```text
/api/v1/payments
```

---

## Verify Payment

**POST**

```text
/api/v1/payments/verify
```

---

# Admin APIs

## Dashboard

**GET**

```text
/api/v1/admin/dashboard
```

---

## Manage Trains

```text
GET    /api/v1/admin/trains

POST   /api/v1/admin/trains

PUT    /api/v1/admin/trains/{id}

DELETE /api/v1/admin/trains/{id}
```

---

## Manage Stations

```text
GET    /api/v1/admin/stations

POST   /api/v1/admin/stations

PUT    /api/v1/admin/stations/{id}

DELETE /api/v1/admin/stations/{id}
```

---

# HTTP Status Codes

| Code | Description           |
| ---- | --------------------- |
| 200  | OK                    |
| 201  | Created               |
| 204  | No Content            |
| 400  | Bad Request           |
| 401  | Unauthorized          |
| 403  | Forbidden             |
| 404  | Not Found             |
| 409  | Conflict              |
| 500  | Internal Server Error |

---

# API Versioning

The API follows URL versioning.

Example:

```text
/api/v1/
/api/v2/
```

This allows future API versions without breaking existing clients.

---

# Security

* JWT Authentication
* HTTPS
* Password Hashing
* Input Validation
* Role-Based Access Control (RBAC)
* CORS Configuration

---

# Future APIs

The following endpoints are planned for future releases:

* Waiting List Management
* RAC Allocation
* Live Train Status
* Notifications
* Reviews & Ratings
* Fare Prediction
* Analytics Dashboard

---

# Version History

| Version | Description                    |
| ------- | ------------------------------ |
| v0.1    | Initial REST API specification |
