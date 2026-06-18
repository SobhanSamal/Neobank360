# NeoBank360

NeoBank360 is a digital banking platform built as a full-stack application. The backend is a Spring Boot REST API and the frontend is built with Angular 21. The system is designed to manage user registration, authentication, account management, and transaction processing.

This README covers everything implemented through the first three days: project setup, database design, registration API, login API, JWT authentication, protected frontend routes, validation rules, security configuration, and test coverage.

---

## Overview

NeoBank360 is split into three main parts:

| Part | Technology | Purpose |
|------|-----------|---------|
| Backend API | Spring Boot 3.2.5, Java 17 | REST API for authentication and banking operations |
| Frontend UI | Angular 21, TypeScript 5.9 | Single-page application for user interaction |
| Database | MySQL | Persistent storage for users, accounts, and transactions |

The platform follows a layered architecture. The frontend talks to the backend via HTTP REST. The backend talks to the MySQL database through Spring Data JPA. All layers are independently testable.

---

## Day 1

### Backend Setup

The backend was initialized as a Maven project using Spring Boot 3.2.5. The project structure follows a standard layered pattern where each layer has a clear responsibility:

- `controller` – receives HTTP requests, delegates to service, returns HTTP responses
- `service` – contains all business logic and rules
- `repository` – Spring Data JPA interfaces for database access
- `entity` – JPA-mapped Java classes that map to database tables
- `dto` – Data Transfer Objects used as request/response bodies in the API
- `config` – Spring configuration beans (security, CORS, encoders)

Java 17 is used as the runtime. All Spring dependencies are managed by the Spring Boot parent so version conflicts are minimized.

### Frontend Setup

Angular 21 project was created using Angular CLI. The application uses standalone components instead of NgModules, which means every component, service, and directive is self-contained.

Key setup decisions:
- `provideRouter()` for route management
- `provideHttpClient(withFetch())` to enable modern fetch-based HTTP
- `provideClientHydration(withEventReplay())` for SSR-compatible hydration

### Database Schema Design

The schema was created in ../../database.sql.

Three tables form the core data model:

**`users` table**
This is the identity table. It stores one record per registered user.
- `id` – auto-increment primary key
- `email` – unique, not null – used as the login identifier
- `password_hash` – BCrypt-encoded password, never stored as plain text
- `full_name` – user display name
- `role` – ENUM with values `ADMIN` or `CUSTOMER`
- `is_active` – boolean flag to enable/disable account
- `created_at` – auto timestamp on insert

**`accounts` table**
Each user can have one or more bank accounts. This table links accounts to users.
- `id` – primary key
- `user_id` – foreign key to `users.id` (cascades on delete)
- `account_number` – unique account identifier
- `balance` – DECIMAL(15, 2) starting at 0.00
- `account_type` – ENUM `SAVINGS` or `CURRENT`
- `created_at` – auto timestamp

**`transactions` table**
Each transaction records a debit or credit against an account. The balance is captured at the time of transaction for historical accuracy.
- `id` – primary key
- `account_id` – foreign key to `accounts.id`
- `type` – ENUM `DEBIT` or `CREDIT`
- `amount` – DECIMAL(15, 2), must be greater than 0 (enforced by CHECK constraint)
- `description` – optional text for transaction note
- `transaction_date` – auto timestamp
- `balance_after` – snapshot of balance after the transaction applied

The schema uses InnoDB for all tables to support foreign key constraints and ACID transactions.

---

## Day 2

---

## Backend (detailed)

### Architecture layers in Day 2

Day 2 implemented the full user registration pipeline across all layers.

```
HTTP Request
     ↓
AuthController (validates input with @Valid)
     ↓
UserService (business rules, hashing, persistence)
     ↓
UserRepository (JPA save/query)
     ↓
MySQL (users table)
     ↑
UserResponse DTO (returned to client)
```

---

### Registration endpoint

**Endpoint:** `POST /api/auth/register`

**Controller file:** ../../Backend/NeoBank360app/src/main/java/com/neobank/controller/AuthController.java

The controller is annotated with `@RestController` and `@RequestMapping("/api/auth")`. It exposes a single method annotated with `@PostMapping("/register")`.

The `@Valid` annotation triggers Spring's built-in validation framework on the incoming `RegisterRequest` DTO. If any constraint fails, a `MethodArgumentNotValidException` is thrown and caught by the local `@ExceptionHandler` method, which collects all field errors into a `Map<String, String>` and returns a `400 Bad Request` response.

If validation passes, the request is forwarded to `UserService.register()`.

The controller wraps service calls in a try-catch to handle business exceptions:
- `IllegalArgumentException` with "already registered" → `409 Conflict`
- `IllegalArgumentException` with "do not match" → `400 Bad Request`
- Any other exception → `500 Internal Server Error`

All responses are structured. Successful registration returns a `UserResponse` object.

---

### Request DTO and input validation

**DTO file:** ../../Backend/NeoBank360app/src/main/java/com/neobank/dto/RegisterRequest.java

`RegisterRequest` is a plain Java class annotated with validation constraints:

| Field | Constraint | Error message |
|-------|-----------|--------------|
| `fullName` | `@NotBlank`, `@Size(min=3, max=255)` | Full name is required; must be 3-255 characters |
| `email` | `@NotBlank`, `@Email` | Email is required; must be valid format |
| `password` | `@NotBlank`, `@Size(min=8)`, `@Pattern` | Min 8 chars, must include upper, lower, digit, special char |
| `confirmPassword` | `@NotBlank` | Confirm password is required |

The password pattern used is:
```
^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z\d]).{8,}$
```

This regex uses lookaheads to enforce that at least one of each character category exists, and a minimum total length of 8. The special character is accepted from any non-alphanumeric character class, not a restricted set.

The DTO uses explicit getters and setters (no Lombok) to avoid IDE annotation processing issues in Spring Tool Suite.

---

### Service layer business rules

**Service file:** ../../Backend/NeoBank360app/src/main/java/com/neobank/service/UserService.java

`UserService.register()` is annotated `@Transactional`. It applies business rules in this order:

**Step 1 – Password match check**
If `password` does not equal `confirmPassword`, an `IllegalArgumentException` is thrown with message "Passwords do not match". This stops processing before any database query.

**Step 2 – Email uniqueness check**
Calls `userRepository.existsByEmail(email)`. If it returns `true`, throws `IllegalArgumentException` with "Email already registered". This prevents duplicate user accounts at the application level (the database also enforces this with a UNIQUE constraint as a second layer).

**Step 3 – Password hashing**
The raw password is passed to `BCryptPasswordEncoder.encode()`. BCrypt is an adaptive hashing algorithm with a built-in salt, which means even identical passwords produce different hashes and the original password cannot be recovered.

**Step 4 – User entity creation and save**
A `User` entity is created, fields populated, and saved via `userRepository.save()`. Default values set explicitly:
- `role = CUSTOMER`
- `isActive = true`

**Step 5 – Return DTO**
The saved entity is converted to a `UserResponse` via the static factory method `UserResponse.fromEntity(savedUser)`. The raw password hash is never included in the response.

---

### Security configuration

**Config file:** ../../Backend/NeoBank360app/src/main/java/com/neobank/config/SecurityConfig.java

The `SecurityConfig` class defines a `SecurityFilterChain` bean that controls all request-level security:

- **CSRF disabled** – not needed for stateless APIs; CSRF tokens are meaningful only for session-based apps
- **CORS configured** – allows `http://localhost:4200` (Angular dev server), all standard HTTP methods, all headers, credentials allowed
- **Session policy: STATELESS** – the server keeps no session state; every request must be independently authenticated (JWT will be added in Day 3)
- **Permit all for auth routes** – `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**` are accessible without authentication
- **Authenticated for everything else** – any other endpoint requires a valid session/token
- **HTTP Basic disabled** – no browser login prompts
- **Form login disabled** – not using default Spring Security login page

A `BCryptPasswordEncoder` bean is defined here and injected wherever password hashing is needed.

---

### API response codes reference

| Scenario | HTTP Status | Response body |
|----------|------------|--------------|
| Registration successful | 201 Created | UserResponse JSON (id, email, fullName, role, isActive, createdAt) |
| Missing or invalid field | 400 Bad Request | Map of field name → error message |
| Passwords do not match | 400 Bad Request | `{ "message": "Passwords do not match", "status": 400 }` |
| Email already registered | 409 Conflict | `{ "message": "Email already registered", "status": 409 }` |
| Server-side exception | 500 Internal Server Error | `{ "message": "Registration failed: ...", "status": 500 }` |

---

### Backend test coverage

Three test classes cover Day 2 backend:

**UserServiceTest** – ../../Backend/NeoBank360app/src/test/java/com/neobank/service/UserServiceTest.java

Uses `@ExtendWith(MockitoExtension.class)` to isolate the service from database. Mocks `UserRepository` and `BCryptPasswordEncoder`.

Test cases:
- `testRegisterSuccessfully` – mocks repository to return false on existsByEmail, mocks encoder, verifies response matches saved user
- `testRegisterWithDuplicateEmail` – mocks existsByEmail to return true, asserts exception thrown, verifies save was never called
- `testRegisterWithPasswordMismatch` – sends mismatching passwords, asserts exception thrown before any repository call
- `testFindByEmailSuccessfully` – mocks findByEmail to return a user, asserts correct entity returned
- `testFindByEmailNotFound` – mocks findByEmail to return empty Optional, asserts exception thrown

**AuthControllerTest** – ../../Backend/NeoBank360app/src/test/java/com/neobank/controller/AuthControllerTest.java

Uses `MockMvc` with standalone setup to test the controller layer independently.

Test cases:
- `testRegisterSuccessfully` – sends valid payload, mocks service to return UserResponse, asserts 201 and JSON fields
- `testRegisterWithDuplicateEmail` – mocks service to throw exception, asserts 409 and message in body
- `testRegisterWithPasswordMismatch` – mocks service to throw exception, asserts 400 and message
- `testRegisterWithInvalidEmail` – sends invalid email, asserts 400 and email error field present
- `testRegisterWithWeakPassword` – sends short password, asserts 400 and password error present
- `testRegisterWithMissingFullName` – sends blank fullName, asserts 400 and fullName error present
- `testRegisterWithMissingEmail` – sends blank email, asserts 400
- `testRegisterWithMissingPassword` – sends blank password, asserts 400

**RegisterRequestValidationTest** – ../../Backend/NeoBank360app/src/test/java/com/neobank/dto/RegisterRequestValidationTest.java

Uses `@SpringBootTest` with injected `Validator` bean to test constraint annotations directly.

Test cases: valid request, blank fullName, short fullName, invalid email, blank email, short password, password without uppercase, password without lowercase, password without digit, password without special char, blank password, blank confirmPassword, password with hash symbol, password with parentheses.

---

## Frontend (detailed)

### Angular application structure

The frontend is a standalone-component Angular 21 application. There are no NgModules. Each feature is a standalone `@Component` or `@Injectable` that declares its own imports.

Key configuration files:

**App config** – src/app/app.config.ts

Registers global providers at application bootstrap:
- `provideRouter(routes)` – connects the route table
- `provideHttpClient(withFetch(), withInterceptors([authInterceptor]))` – enables fetch-based HTTP and registers JWT interceptor
- `provideClientHydration(withEventReplay())` – SSR hydration support

**Routes** – src/app/app.routes.ts

| Path | Component | Behavior |
|------|-----------|---------|
| `` (empty) | redirect | redirects to `/login` |
| `register` | RegisterComponent | user registration form |
| `login` | LoginComponent | user login form |
| `dashboard` | DashboardComponent | protected route (`authGuard`) |
| `**` | redirect | all unknown paths go to login |

---

### Register component

**File:** src/app/auth/register/register.component.ts

This component uses reactive forms with field validators and a cross-field password match validator. On successful registration, users are redirected to `/login`.

---

### Day 3 Login component

**Files:**
- src/app/auth/login/login.component.ts
- src/app/auth/login/login.component.html
- src/app/auth/login/login.component.css

Login flow:
1. User submits `email` and `password`
2. `AuthService.login()` calls backend `POST /api/auth/login`
3. On success, token/user info is stored in localStorage
4. User is redirected to `/dashboard`

---

### Auth service

**File:** src/app/services/auth.service.ts

`AuthService` now supports both registration and login.

Methods:
- `register(request)`
- `login(request)`
- `saveSession(response)`
- `getToken()`
- `getUserEmail()`
- `isLoggedIn()`
- `logout()`

Token/session keys:
- `nb360_token`
- `nb360_email`
- `nb360_role`

---

### Route protection and token forwarding

**Guard** – src/app/auth/auth.guard.ts
- Prevents unauthorized access to `/dashboard`

**Interceptor** – src/app/auth/auth.interceptor.ts
- Adds `Authorization: Bearer <token>` to outgoing backend API requests

**Dashboard** – src/app/dashboard/dashboard.component.ts
- Displays logged-in user email and supports logout

---

## Day 3 Backend Security and JWT

### Authentication endpoint
- `POST /api/auth/login`
- Implemented in ../../Backend/NeoBank360app/src/main/java/com/neobank/controller/AuthController.java

### DTOs
- ../../Backend/NeoBank360app/src/main/java/com/neobank/dto/LoginRequest.java
- ../../Backend/NeoBank360app/src/main/java/com/neobank/dto/LoginResponse.java

### JWT components
- ../../Backend/NeoBank360app/src/main/java/com/neobank/service/JwtService.java
- ../../Backend/NeoBank360app/src/main/java/com/neobank/security/JwtAuthenticationFilter.java

### Security configuration
- File: ../../Backend/NeoBank360app/src/main/java/com/neobank/config/SecurityConfig.java
- Public endpoints:
     - `/api/auth/register`
     - `/api/auth/login`
- Role-based access:
     - `/api/customer/**` -> CUSTOMER or ADMIN
     - `/api/admin/**` -> ADMIN only

### Sample protected controller
- ../../Backend/NeoBank360app/src/main/java/com/neobank/controller/DashboardController.java

---

## Versions

### Backend
- Java 17
- Spring Boot 3.2.5
- JJWT 0.11.5
- Spring Security
- Spring Data JPA

### Frontend
- Angular CLI 21.0.4
- Angular 21.x
- TypeScript ~5.9.2
- RxJS ~7.8.0
- Vitest (unit test runner)

---

## Run

### Frontend
```bash
npm install
ng serve
```
Access at: `http://localhost:4200`

### Backend
```bash
mvn clean compile
mvn spring-boot:run
```
API base URL: `http://localhost:8080`

If Maven Central access is blocked (PKIX/403), use STS Maven Offline mode and re-run Maven Update Project with force update.

---

## Project folder structure

```text
Neo BANK 360/
├── database.sql
├── Backend/
│   └── NeoBank360app/
│       ├── pom.xml
│       └── src/
│           ├── main/java/com/neobank/
│           │   ├── config/SecurityConfig.java
│           │   ├── controller/AuthController.java
│           │   ├── controller/DashboardController.java
│           │   ├── dto/RegisterRequest.java
│           │   ├── dto/LoginRequest.java
│           │   ├── dto/LoginResponse.java
│           │   ├── dto/UserResponse.java
│           │   ├── security/JwtAuthenticationFilter.java
│           │   ├── service/UserService.java
│           │   ├── service/JwtService.java
│           │   ├── repository/UserRepository.java
│           │   ├── entity/User.java
│           │   ├── entity/Account.java
│           │   └── entity/Transaction.java
│           └── test/java/com/neobank/
│               ├── controller/AuthControllerTest.java
│               ├── dto/RegisterRequestValidationTest.java
│               └── service/UserServiceTest.java
└── Frontend/
          └── NeoBank360_UI/
                    └── src/app/
                              ├── app.config.ts
                              ├── app.routes.ts
                              ├── auth/register/register.component.ts
                              ├── auth/login/login.component.ts
                              ├── auth/auth.guard.ts
                              ├── auth/auth.interceptor.ts
                              ├── dashboard/dashboard.component.ts
                              └── services/auth.service.ts
```

---

## Additional Theory (Deep Explanation)

### 1) Why layered architecture is critical in banking systems

In banking software, direct coupling between UI, business rules, and database code creates high operational risk. A layered architecture reduces this risk by introducing strict responsibility boundaries:

- Controller layer handles protocol concerns (HTTP request/response mapping).
- Service layer handles domain behavior (validation sequencing, business decisions, orchestration).
- Repository layer handles persistence abstraction and query execution.
- Entity layer models storage structure.
- DTO layer models API contracts and prevents data leakage.

This separation lowers bug propagation. For example, a UI change does not force data-model rewrites, and persistence tuning does not alter API contracts.

### 2) Registration theory: correctness before persistence

Registration is not only a data insert operation. It is a guarded state transition from "unknown identity" to "active customer identity". Correctness is achieved by validating in stages:

1. Shape validation (required fields, format, length)
2. Semantic validation (password and confirmPassword must match)
3. Uniqueness validation (email must not already exist)
4. Security transformation (hash password before storage)
5. Persistence and response mapping

This order avoids unnecessary database writes, ensures deterministic failure behavior, and protects credentials.

### 3) Authentication theory: identity proof vs permission proof

Authentication answers: "Who are you?"
Authorization answers: "What can you access?"

Day 3 implements both:
- Login proves identity using email + password verification.
- JWT carries claims (such as role) used to enforce permissions.

A common security error is treating authentication success as universal authorization. Role checks prevent this by restricting protected resources according to policy.

### 4) JWT security model

JWT is signed, not encrypted by default. Therefore:
- token content is integrity-protected,
- token content is not secret by itself,
- only trusted claims should be included.

Important design implications:
- Keep token expiry finite to reduce replay window.
- Validate signature and expiry on every protected request.
- Reject missing or malformed Bearer tokens early.

### 5) Stateless security and scalability

Stateful sessions require server memory coordination. Stateless JWT moves session proof to the client token and allows each request to be independently validated. This improves horizontal scaling because authentication state does not need centralized session storage.

Trade-off: token revocation is harder compared to server-side sessions. In larger systems, this is solved with short token lifetimes, rotating keys, and token blacklist strategies for critical revocations.

### 6) Frontend security behavior theory

Frontend security is behavioral, not trust-based:
- Route guard improves UX by preventing accidental unauthorized navigation.
- Interceptor ensures token forwarding is consistent and centralized.

However, frontend checks are never a replacement for backend enforcement. Backend authorization remains the final control boundary.

### 7) Validation design philosophy

Validation is deliberately duplicated at multiple layers:
- Client-side validation: speed and user guidance.
- Server-side DTO validation: trust boundary enforcement.
- Service-level validation: business invariant enforcement.
- Database constraints: final integrity guard.

This defense-in-depth strategy is essential for financial systems where input mistakes or malicious payloads can have real-world impact.

### 8) Error handling strategy

Reliable APIs classify failures clearly:
- 4xx for caller-correctable errors (validation/auth issues)
- 5xx for server-side unexpected failures

Clear status mapping enables predictable client behavior, better monitoring, and faster incident triage.

### 9) Testing strategy theory

The project test structure aligns with risk distribution:
- Unit tests target business rules in isolation.
- Controller tests verify HTTP contract and status mapping.
- Validation tests verify field constraint correctness.

This layered testing approach minimizes false confidence while keeping execution speed practical.

### 10) Architectural readiness after Day 3

By the end of Day 3, the system is ready for core banking features such as:
- account creation workflows,
- balance mutation safeguards,
- transaction posting with idempotency protections,
- audit logging,
- role-specific dashboards and operations.

The key strength is that authentication and authorization foundations are already in place, enabling secure expansion without architectural rework.