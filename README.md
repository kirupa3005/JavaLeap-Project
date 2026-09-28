# SHIFTPLANNER – Employee Shift Roster & Shift Swap Management System

SHIFTPLANNER is a beginner-friendly Spring Boot backend application designed to manage employee work shifts, weekly rosters, and peer-to-peer shift swap requests with role-based security and JWT authentication.

---

## Table of Contents
1. [Project Overview](#1-project-overview)
2. [Features](#2-features)
3. [Technology Stack](#3-technology-stack)
4. [Project Structure](#4-project-structure)
5. [Database Setup](#5-database-setup)
6. [How JWT Authentication Works](#6-how-jwt-authentication-works)
7. [How to Run the Project](#7-how-to-run-the-project)
8. [API Endpoints Reference](#8-api-endpoints-reference)
9. [Sample JSON Requests & Responses](#9-sample-json-requests--responses)
10. [Step-by-Step Postman Testing Guide](#10-step-by-step-postman-testing-guide)
11. [Complete Shift Swap Workflow Example](#11-complete-shift-swap-workflow-example)
12. [Viva Questions & Concept Explanations](#12-viva-questions--concept-explanations)

---

## 1. Project Overview

In organizations such as retail stores, hospitals, or customer support centers, employees are scheduled to work shifts (e.g., Morning, Afternoon, Night). Often, employees need to swap shifts due to personal appointments or emergencies. 

**SHIFTPLANNER** handles this end-to-end:
- **Managers** configure shift timings, register employees, and assign shifts to dates (Roster).
- **Employees** view their upcoming rosters and can request a shift swap directly with another colleague.
- **The Target Employee** can review, accept, or reject the swap.
- Once accepted, the system validates that the target employee does not already have a conflicting shift on that date, and automatically reassigns the roster.

---

## 2. Features

- **User Authentication & Role-Based Authorization**:
  - Secure registration and login.
  - Passwords hashed using **BCrypt**.
  - Stateless **JWT (JSON Web Token)** issued upon login.
  - Two roles: `MANAGER` and `EMPLOYEE`.
- **Shift Management (CRUD)**:
  - Create, view, update, and delete shifts with start and end times.
- **Roster Management (CRUD)**:
  - Assign employees to shifts on specific calendar dates.
  - **Conflict Prevention Rule**: An employee cannot have more than one shift on the same date.
  - Employees can fetch their personalized roster via `/api/rosters/my`.
- **Shift Swap Workflow**:
  - Employees can create swap requests for their assigned shifts.
  - Enforces that employees can only swap their own shifts.
  - Prevents swapping with oneself.
  - Target employee can accept or reject the swap.
  - When accepted, the roster ownership updates automatically in a single atomic transaction.
- **Clean Architecture & Exception Handling**:
  - Centralized `@RestControllerAdvice` translates business logic violations into clean JSON errors with HTTP status codes (400, 401, 403, 404).

---

## 3. Technology Stack

- **Java**: 17 or 21
- **Spring Boot**: 3.2.x
- **Spring Web**: Building RESTful APIs with `@RestController`
- **Spring Data JPA & Hibernate**: ORM for MySQL database operations
- **Spring Security & JJWT**: Authentication and stateless token authorization
- **Jakarta Bean Validation**: Request input validation (`@NotBlank`, `@NotNull`, `@Email`, `@Size`)
- **MySQL**: Relational database
- **Maven**: Dependency and build management

> **No Lombok or Over-engineering**: Standard plain Java constructors, getters, and setters are used so any 2nd-year computer science student can easily explain every line during viva.

---

## 4. Project Structure

```
src/main/java/com/example/shiftplanner/
│
├── ShiftPlannerApplication.java            # Main application entry point
│
├── config/
│   └── SecurityConfig.java                 # Spring Security & filter chain configuration
│
├── controller/
│   ├── AuthController.java                 # Login & Registration endpoints
│   ├── EmployeeController.java             # Employee profile & manager CRUD
│   ├── ShiftController.java                # Shift timings CRUD
│   ├── RosterController.java               # Roster scheduling & personal roster
│   └── SwapRequestController.java          # Shift swap requests & approvals
│
├── dto/
│   ├── RegisterRequest.java                # Registration payload
│   ├── LoginRequest.java                   # Login credentials payload
│   ├── LoginResponse.java                  # JWT token response
│   ├── EmployeeRequest.java                # Employee creation/update payload
│   ├── EmployeeResponse.java               # Clean employee response (no password)
│   ├── ShiftRequest.java                   # Shift create/update payload
│   ├── RosterRequest.java                  # Roster assignment payload
│   ├── RosterResponse.java                 # Clean roster details DTO
│   ├── SwapRequestDto.java                 # Swap initiation payload
│   └── SwapResponseDto.java                # Swap details & status DTO
│
├── entity/
│   ├── Employee.java                       # Employee table mapping
│   ├── Shift.java                          # Shift definitions
│   ├── Roster.java                         # Employee-Shift-Date assignment
│   ├── SwapRequest.java                    # Swap request details & status
│   ├── Role.java                           # Enum: EMPLOYEE, MANAGER
│   └── SwapStatus.java                     # Enum: PENDING, ACCEPTED, REJECTED
│
├── repository/
│   ├── EmployeeRepository.java             # Spring Data JPA repository for Employee
│   ├── ShiftRepository.java                # Spring Data JPA repository for Shift
│   ├── RosterRepository.java               # Spring Data JPA repository for Roster
│   └── SwapRequestRepository.java          # Spring Data JPA repository for SwapRequest
│
├── security/
│   ├── JwtService.java                     # Token generation, claims extraction, validation
│   ├── CustomUserDetailsService.java       # Loads user details by email for Spring Security
│   └── JwtAuthenticationFilter.java        # OncePerRequestFilter parsing Authorization header
│
├── service/
│   ├── AuthService.java                    # Register, login, password hashing
│   ├── EmployeeService.java                # Employee CRUD & profile business logic
│   ├── ShiftService.java                   # Shift business logic
│   ├── RosterService.java                  # Roster creation with date conflict checks
│   └── SwapRequestService.java             # Swap workflow & business validation rules
│
└── exception/
    ├── ResourceNotFoundException.java      # Thrown when entity ID is not found (404)
    ├── BusinessException.java              # Thrown when business rules fail (400)
    └── GlobalExceptionHandler.java         # Centralized error handler with @RestControllerAdvice
```

---

## 5. Database Setup

1. Make sure MySQL Server is running on your machine.
2. Open MySQL Workbench, MySQL Command Line Client, or your terminal:
   ```sql
   CREATE DATABASE IF NOT EXISTS shiftplanner;
   ```
3. Open `src/main/resources/application.properties` and verify your MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/shiftplanner?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```
4. Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update`. When you run the application, Hibernate will automatically create all four tables (`employees`, `shifts`, `rosters`, `swap_requests`).

---

## 6. How JWT Authentication Works

```
Client (Postman/Browser)                 Server (Spring Boot)
       |                                          |
       |----- POST /api/auth/login -------------->|
       |      { email, password }                 | 1. Find user by email
       |                                          | 2. Compare password with BCrypt
       |                                          | 3. Generate JWT with claims (id, email, role)
       |<---- 200 OK + JWT Token -----------------|
       |                                          |
       |----- GET /api/rosters/my --------------->|
       |      Header: Authorization: Bearer <JWT> | 1. JwtAuthenticationFilter intercepts request
       |                                          | 2. Validates token signature & expiration
       |                                          | 3. Extracts email, loads user roles
       |                                          | 4. Stores Authentication in SecurityContext
       |                                          | 5. Service checks permissions & executes
       |<---- 200 OK + Personal Roster JSON ------|
```

1. **Token Structure**:
   A JWT has 3 parts separated by dots (`.`):
   - **Header**: Contains the algorithm (`HS256`).
   - **Payload (Claims)**: Contains user ID, email, role (`ROLE_EMPLOYEE` or `ROLE_MANAGER`), and expiration timestamp.
   - **Signature**: Generated using a secret key stored on the server to prevent tampering.
2. **Stateless Nature**:
   The server doesn't store session IDs in memory or database. Every protected request carries the token in the `Authorization: Bearer <token>` HTTP header.

---

## 7. How to Run the Project

### Using Command Line (Maven):
```bash
mvn clean compile
mvn spring-boot:run
```

The server will start on port `8080`: `http://localhost:8080`.

---

## 8. API Endpoints Reference

### Authentication (`/api/auth`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register a new Employee or Manager |
| `POST` | `/api/auth/login` | Public | Login with email and password to receive JWT |

### Shifts (`/api/shifts`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/shifts` | Authenticated | View all shifts |
| `GET` | `/api/shifts/{id}` | Authenticated | View shift details by ID |
| `POST` | `/api/shifts` | `MANAGER` | Create a new shift timing |
| `PUT` | `/api/shifts/{id}` | `MANAGER` | Update shift details |
| `DELETE` | `/api/shifts/{id}` | `MANAGER` | Delete a shift |

### Employees (`/api/employees`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/employees/me` | Authenticated | Get current logged-in employee profile |
| `GET` | `/api/employees` | `MANAGER` | List all employees |
| `GET` | `/api/employees/{id}` | `MANAGER` | Get employee by ID |
| `POST` | `/api/employees` | `MANAGER` | Create employee manually |
| `PUT` | `/api/employees/{id}` | `MANAGER` | Update employee information |
| `DELETE` | `/api/employees/{id}` | `MANAGER` | Delete employee record |

### Rosters (`/api/rosters`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/rosters/my` | Authenticated | Get current employee's shift roster |
| `GET` | `/api/rosters` | `MANAGER` | View all roster assignments |
| `GET` | `/api/rosters/{id}` | `MANAGER` | View specific roster assignment |
| `POST` | `/api/rosters` | `MANAGER` | Assign employee to shift on a date |
| `PUT` | `/api/rosters/{id}` | `MANAGER` | Update roster assignment |
| `DELETE` | `/api/rosters/{id}` | `MANAGER` | Remove roster assignment |

### Swap Requests (`/api/swaps`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/swaps` | Authenticated | Create a swap request for own roster |
| `GET` | `/api/swaps` | `MANAGER` | View all swap requests across organization |
| `GET` | `/api/swaps/my` | Authenticated | View swap requests sent or received by user |
| `GET` | `/api/swaps/{id}` | Authenticated | View single swap request details |
| `PUT` | `/api/swaps/{id}/accept` | Target Employee | Target employee accepts swap (reassigns roster) |
| `PUT` | `/api/swaps/{id}/reject` | Target Employee | Target employee rejects swap (roster unchanged) |

---

## 9. Sample JSON Requests & Responses

### 1. Register User
`POST http://localhost:8080/api/auth/register`
```json
{
    "name": "Benny",
    "email": "benny@gmail.com",
    "password": "password123",
    "phone": "9876543210",
    "role": "EMPLOYEE"
}
```
**Response (201 Created):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "message": "User registered successfully"
}
```

### 2. Login
`POST http://localhost:8080/api/auth/login`
```json
{
    "email": "benny@gmail.com",
    "password": "password123"
}
```
**Response (200 OK):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "message": "Login successful"
}
```

### 3. Create Shift (Manager)
`POST http://localhost:8080/api/shifts`
`Authorization: Bearer <MANAGER_TOKEN>`
```json
{
    "shiftName": "Morning Shift",
    "startTime": "09:00",
    "endTime": "17:00"
}
```
**Response (201 Created):**
```json
{
    "id": 1,
    "shiftName": "Morning Shift",
    "startTime": "09:00",
    "endTime": "17:00"
}
```

### 4. Create Roster (Manager)
`POST http://localhost:8080/api/rosters`
`Authorization: Bearer <MANAGER_TOKEN>`
```json
{
    "employeeId": 2,
    "shiftId": 1,
    "date": "2026-10-05"
}
```
**Response (201 Created):**
```json
{
    "id": 1,
    "employeeId": 2,
    "employeeName": "Benny",
    "employeeEmail": "benny@gmail.com",
    "shiftId": 1,
    "shiftName": "Morning Shift",
    "startTime": "09:00",
    "endTime": "17:00",
    "date": "2026-10-05"
}
```

### 5. Create Swap Request (Employee A)
`POST http://localhost:8080/api/swaps`
`Authorization: Bearer <BENNY_TOKEN>`
```json
{
    "rosterId": 1,
    "requestedToId": 3,
    "reason": "I have a personal appointment"
}
```
**Response (201 Created):**
```json
{
    "id": 1,
    "rosterId": 1,
    "requestedById": 2,
    "requestedByName": "Benny",
    "requestedToId": 3,
    "requestedToName": "Arun",
    "shiftName": "Morning Shift",
    "shiftDate": "2026-10-05",
    "reason": "I have a personal appointment",
    "status": "PENDING"
}
```

### 6. Accept Swap Request (Employee B)
`PUT http://localhost:8080/api/swaps/1/accept`
`Authorization: Bearer <ARUN_TOKEN>`

**Response (200 OK):**
```json
{
    "id": 1,
    "rosterId": 1,
    "requestedById": 2,
    "requestedByName": "Benny",
    "requestedToId": 3,
    "requestedToName": "Arun",
    "shiftName": "Morning Shift",
    "shiftDate": "2026-10-05",
    "reason": "I have a personal appointment",
    "status": "ACCEPTED"
}
```

---

## 10. Step-by-Step Postman Testing Guide

1. Open Postman.
2. Click **Import** (top left) and select the file:
   `ShiftPlanner_Postman_Collection.json` located in the root project folder.
3. The collection has built-in JavaScript test scripts:
   - When you run **Login Manager**, the returned JWT token is automatically saved into the collection variable `token`.
   - Every request in the collection automatically uses `Authorization: Bearer {{token}}`.
4. If you switch between users (e.g. Employee A and Employee B), simply run **Login Employee A** or **Login Employee B**, and the `token` variable will automatically update for your subsequent requests!

---

## 11. Complete Shift Swap Workflow Example

Follow this exact workflow to test or demonstrate the application during viva:

1. **Register Manager**:
   - `POST /api/auth/register` with role `MANAGER` (`manager@example.com`).
2. **Register Employee A**:
   - `POST /api/auth/register` with role `EMPLOYEE` (`benny@gmail.com`). (Assigned ID = 2)
3. **Register Employee B**:
   - `POST /api/auth/register` with role `EMPLOYEE` (`arun@gmail.com`). (Assigned ID = 3)
4. **Login as Manager**:
   - `POST /api/auth/login` (`manager@example.com`).
5. **Create Shifts**:
   - `POST /api/shifts` -> "Morning Shift" (09:00 - 17:00).
   - `POST /api/shifts` -> "Evening Shift" (17:00 - 01:00).
6. **Assign Roster**:
   - `POST /api/rosters` -> Employee A (ID 2), Shift 1, Date `2026-10-05`.
7. **Login as Employee A (Benny)**:
   - `POST /api/auth/login` (`benny@gmail.com`).
8. **Check Benny's Roster**:
   - `GET /api/rosters/my` -> Shows Morning Shift on `2026-10-05`.
9. **Benny Requests Swap with Arun**:
   - `POST /api/swaps` with `rosterId: 1`, `requestedToId: 3`, `reason: "Doctor appointment"`.
   - Status is now `PENDING`.
10. **Login as Employee B (Arun)**:
    - `POST /api/auth/login` (`arun@gmail.com`).
11. **Arun Checks Swaps**:
    - `GET /api/swaps/my` -> Arun sees the pending request from Benny.
12. **Arun Accepts the Swap**:
    - `PUT /api/swaps/1/accept`.
    - Status changes to `ACCEPTED`.
13. **Verify Roster Update**:
    - `GET /api/rosters/my` (logged in as Arun) -> The `2026-10-05` Morning Shift is now listed under Arun!
    - `GET /api/rosters/1` (logged in as Manager) -> Shows `employeeId: 3` (Arun). Benny has been successfully replaced.

---

## 12. Viva Questions & Concept Explanations

Here are the most common questions external examiners ask in computer science vivas, with direct answers based on this project:

### Q1: What is Spring Boot and how is it different from traditional Spring?
**Answer**: Spring Boot simplifies Spring application development by providing **auto-configuration**, **starter dependencies** (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`), and an **embedded Tomcat server**. In traditional Spring, we had to write hundreds of lines of XML or Java configuration and configure external web servers like Tomcat manually.

### Q2: What is the purpose of Spring Data JPA and Hibernate?
**Answer**: 
- **JPA (Jakarta Persistence API)** is a Java specification for Object-Relational Mapping (ORM).
- **Hibernate** is the implementation that translates Java object interactions into MySQL SQL queries.
- **Spring Data JPA** provides repository interfaces (like `JpaRepository<Employee, Long>`). We only declare method signatures such as `findByEmail(String email)` or `existsByEmployeeIdAndDate(...)`, and Spring dynamically generates the underlying SQL queries at runtime.

### Q3: How does JWT authentication work and why is it preferred over HTTP Sessions?
**Answer**: In traditional session authentication, the server stores session IDs in memory/RAM. In a distributed or modern REST architecture, this makes scaling hard. JWT is **stateless**: the user's identity, role, and expiration are cryptographically signed and stored on the client. On every request, the client sends `Authorization: Bearer <token>`, and our `JwtAuthenticationFilter` validates the signature using our secret key without querying a session table.

### Q4: What does `@RestControllerAdvice` do?
**Answer**: `@RestControllerAdvice` allows us to write global, centralized exception handling. Instead of writing `try-catch` blocks inside every controller method, any exception thrown from the Service or Controller layer (e.g. `ResourceNotFoundException`, `BusinessException`) is intercepted by `GlobalExceptionHandler`. It maps each exception to an appropriate HTTP status code (such as 400 Bad Request or 404 Not Found) and formats a clean JSON error response.

### Q5: How is password security implemented?
**Answer**: We use Spring Security's `BCryptPasswordEncoder`. During registration, the plain password passes through a one-way hashing algorithm with automatic salting before saving to MySQL. During login, `passwordEncoder.matches(rawPassword, encodedPassword)` verifies the user. Plain-text passwords are never stored in the database and never returned in DTO responses.

### Q6: What business rules did you enforce in the service layer?
**Answer**:
1. **No double booking**: An employee cannot have more than one shift on the same date.
2. **Ownership rule**: An employee can only request swaps for shifts assigned to themselves.
3. **No self-swap**: Requester and target employee cannot be the same person.
4. **Target employee authority**: Only the target employee (`requestedTo`) is authorized to accept or reject the swap.
5. **Status check**: Only `PENDING` swaps can be accepted or rejected.
6. **Target conflict rule**: Before accepting, the system verifies that the target employee does not already have a shift scheduled on that date.
7. **Atomic update**: Acceptance updates the roster assignment to the target employee in a single transaction (`@Transactional`).
# JavaLeap-Project
# JavaLeap-Project
