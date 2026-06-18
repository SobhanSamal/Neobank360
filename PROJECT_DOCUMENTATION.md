# NeoBank360 — Full Project Documentation
## 10-Day Curriculum: Spring Boot + Angular Banking Application

> **Build Status:** ✅ Frontend (`ng build`) — Pass | ✅ Backend API smoke-test — Pass  
> **Last verified:** April 22, 2026

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Tech Stack](#2-tech-stack)
3. [Project Structure](#3-project-structure)
4. [Database Schema](#4-database-schema)
5. [Day-by-Day Implementation](#5-day-by-day-implementation)
   - [Day 1 — Environment & Database Setup](#day-1--environment--database-setup)
   - [Day 2 — User Registration](#day-2--user-registration)
   - [Day 3 — Secure Login & JWT Authentication](#day-3--secure-login--jwt-authentication)
   - [Day 4 — RBAC & Profile Management](#day-4--rbac--profile-management)
   - [Day 5 — Account Module Backend](#day-5--account-module-backend)
   - [Day 6 — Account Module Frontend](#day-6--account-module-frontend)
   - [Day 7 — Transaction Engine](#day-7--transaction-engine)
   - [Day 8 — Dashboard (Customer & Admin)](#day-8--dashboard-customer--admin)
   - [Day 9 — Transaction UI (Grid, Filter, Sort)](#day-9--transaction-ui-grid-filter-sort)
   - [Day 10 — Testing & Quality](#day-10--testing--quality)
6. [API Reference](#6-api-reference)
7. [Security Architecture](#7-security-architecture)
8. [Running the Application](#8-running-the-application)
9. [Completion Status Summary](#9-completion-status-summary)

---

## 1. Project Overview

**NeoBank360** is a full-stack digital banking platform built over a 10-day curriculum. It simulates a real-world neobanking application with:

- JWT-secured REST APIs using Spring Boot 3.2.5
- Reactive Angular 17+ frontend with standalone components
- Role-based access control (CUSTOMER / ADMIN)
- Account creation, deposit, withdrawal, and transaction history
- Real-time balance updates across components using an EventEmitter service
- Dedicated admin dashboard with account monitoring

---

## 2. Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Backend Framework | Spring Boot | 3.2.5 |
| Language (Backend) | Java | 17 |
| Security | Spring Security + JJWT | 0.11.5 |
| ORM | Spring Data JPA + Hibernate | Auto |
| Database | MySQL | 8.0+ |
| Frontend Framework | Angular (Standalone) | 17+ |
| Language (Frontend) | TypeScript | 5.x |
| HTTP Client | Angular HttpClient + RxJS | 7.8 |
| Build Tool (Backend) | Maven | 3.x (mvnw) |
| Build Tool (Frontend) | Angular CLI | Latest |
| Session Storage | Browser sessionStorage | — |
| Token Format | JWT HS256 | — |

---

## 3. Project Structure

```
Neo BANK 360/
├── Backend/
│   └── NeoBank360app/
│       ├── pom.xml
│       └── src/main/java/com/neobank/
│           ├── config/
│           │   ├── SecurityConfig.java        # Spring Security + CORS + JWT filter
│           │   └── DataInitializer.java       # Seeds admin@neobank.com on startup
│           ├── controller/
│           │   ├── AuthController.java        # POST /api/auth/register & /login
│           │   ├── UserController.java        # GET/PUT /api/users/me
│           │   ├── AdminController.java       # GET /api/admin/users
│           │   ├── AccountController.java     # CRUD /api/accounts
│           │   ├── TransactionController.java # POST deposit/withdraw, GET history
│           │   └── DashboardController.java   # Basic protected dashboard endpoints
│           ├── dto/
│           │   ├── RegisterRequest.java
│           │   ├── LoginRequest.java / LoginResponse.java
│           │   ├── UserResponse.java / UpdateProfileRequest.java
│           │   ├── AccountResponse.java / CreateAccountRequest.java
│           │   └── TransactionRequest.java / TransactionResponse.java
│           ├── entity/
│           │   ├── User.java                  # id, email, passwordHash, role, isActive
│           │   ├── Account.java               # id, user(FK), accountNumber, balance, type
│           │   └── Transaction.java           # id, account(FK), type, amount, balanceAfter
│           ├── repository/
│           │   ├── UserRepository.java
│           │   ├── AccountRepository.java
│           │   └── TransactionRepository.java
│           ├── security/
│           │   └── JwtAuthenticationFilter.java
│           └── service/
│               ├── UserService.java
│               ├── AccountService.java
│               ├── TransactionService.java
│               └── JwtService.java
│
├── Frontend/
│   └── NeoBank360_UI/
│       └── src/app/
│           ├── auth/
│           │   ├── register/                  # RegisterComponent
│           │   ├── login/                     # LoginComponent
│           │   ├── auth.guard.ts              # CanActivateFn
│           │   └── auth.interceptor.ts        # HttpInterceptorFn (adds Bearer token)
│           ├── guards/
│           │   └── role.guard.ts              # Role-based route guard
│           ├── interceptors/
│           │   └── token-expiry.interceptor.ts # Auto-logout on expired token
│           ├── services/
│           │   ├── auth.service.ts            # Login/logout, JWT, sessionStorage
│           │   ├── user-api.service.ts        # All HTTP calls to backend
│           │   └── account-balance.service.ts # EventEmitter for real-time balance
│           ├── dashboard/
│           │   ├── customer/                  # CustomerDashboardComponent
│           │   └── admin/                     # AdminDashboardComponent
│           ├── account/                       # AccountComponent (full KYC form)
│           ├── transaction/                   # TransactionComponent (deposit/withdraw/history)
│           ├── user/profile/                  # ProfileComponent
│           ├── navbar/                        # NavbarComponent
│           ├── landing/                       # LandingComponent
│           └── app.routes.ts                  # Route definitions with guards
│
└── database.sql                               # Reference schema
```

---

## 4. Database Schema

```sql
-- users
CREATE TABLE users (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  email        VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  full_name    VARCHAR(255) NOT NULL,
  role         ENUM('CUSTOMER','ADMIN') DEFAULT 'CUSTOMER',
  is_active    BOOLEAN DEFAULT TRUE,
  created_at   DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- accounts
CREATE TABLE accounts (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id        BIGINT NOT NULL REFERENCES users(id),
  account_number VARCHAR(20) UNIQUE NOT NULL,    -- e.g. NB + 18 hex chars
  balance        DECIMAL(19,2) DEFAULT 0.00,
  account_type   ENUM('SAVING','CURRENT'),
  created_at     DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- transactions
CREATE TABLE transactions (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_id       BIGINT NOT NULL REFERENCES accounts(id),
  type             ENUM('CREDIT','DEBIT') NOT NULL,
  amount           DECIMAL(19,2) NOT NULL,
  description      VARCHAR(500),
  balance_after    DECIMAL(19,2) NOT NULL,
  transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

**Seeded data (via `DataInitializer.java`):**
- `admin@neobank.com` / `admin123` → role `ADMIN`, seeded on every startup if absent

---

## 5. Day-by-Day Implementation

---

### Day 1 — Environment & Database Setup
**Status: ✅ Complete**

#### Objectives
Set up the full development environment and define the core database entities.

#### Backend Deliverables
| File | Description |
|------|-------------|
| `pom.xml` | Spring Boot 3.2.5, Spring Web, Security, Data JPA, MySQL, Lombok, JJWT 0.11.5, Validation, Test |
| `NeoBank360appApplication.java` | Spring Boot main class |
| `application.properties` | JDBC datasource, `ddl-auto=update`, JWT secret config |
| `User.java` | Entity — id, email (UNIQUE), passwordHash, fullName, role (ENUM), isActive, createdAt |
| `Account.java` | Entity — id, user (ManyToOne FK), accountNumber (UNIQUE), balance (DECIMAL), accountType (ENUM) |
| `Transaction.java` | Entity — id, account (ManyToOne FK), type (ENUM), amount, description, balanceAfter, transactionDate |

#### Frontend Deliverables
| File | Description |
|------|-------------|
| `angular.json` | Angular project configuration |
| `package.json` | Angular CLI, TypeScript 5.x, RxJS 7.8 |
| `tsconfig.json` | TypeScript strict mode |
| `main.ts` | Angular bootstrap |
| `app.routes.ts` | Route definitions (initial setup) |

#### Key Concepts
- JPA auto-DDL creates tables on first run
- `Role` enum defined as `CUSTOMER` / `ADMIN` on the `User` entity
- `AccountType` enum defined as `SAVING` / `CURRENT` on the `Account` entity
- `TransactionType` enum defined as `CREDIT` / `DEBIT` on the `Transaction` entity

---

### Day 2 — User Registration
**Status: ✅ Complete**

#### Objectives
Implement user registration with server-side validation, email uniqueness check, and BCrypt password hashing.

#### Backend Deliverables
| File | Description |
|------|-------------|
| `RegisterRequest.java` | DTO — fullName (`@NotBlank @Size(min=3)`), email (`@Email`), password (`@Pattern` complexity), confirmPassword |
| `UserResponse.java` | Response DTO — id, email, fullName, role, isActive, createdAt (password excluded) |
| `UserRepository.java` | `findByEmail()`, `existsByEmail()` |
| `UserService.register()` | Validates passwords match → checks email uniqueness → BCrypt hash → save |
| `AuthController.POST /api/auth/register` | 201 on success, 409 on duplicate email, 400 on validation failure |

#### Frontend Deliverables
| File | Description |
|------|-------------|
| `register.component.ts` | Reactive form — fullName, email, password, confirmPassword with cross-field `passwordMatch` validator |
| `register.component.html` | Form template with per-field error messages |
| `register.component.css` | Card/form styling |
| `auth.service.ts` | `register(request)` — HTTP POST + error parsing |

#### Validation Rules
- `fullName`: min 3 chars, max 100 chars, not blank
- `email`: valid email format
- `password`: min 8 chars, must contain uppercase, lowercase, digit, and special character
- `confirmPassword`: must match password (cross-field validator on frontend)

---

### Day 3 — Secure Login & JWT Authentication
**Status: ✅ Complete**

#### Objectives
Implement JWT-based login, protect routes with an auth guard, and attach the JWT to every HTTP request via an interceptor.

#### Backend Deliverables
| File | Description |
|------|-------------|
| `JwtService.java` | `generateToken(user)` — HS256, 24h expiry; `extractEmail(token)`; `isTokenValid()` |
| `JwtAuthenticationFilter.java` | `OncePerRequestFilter` — extracts Bearer token → validates → sets `SecurityContext` with `ROLE_<role>` authority |
| `UserService.login()` | Email lookup → isActive check → BCrypt match → generate JWT → `LoginResponse` |
| `AuthController.POST /api/auth/login` | Returns `{ token, tokenType, email, role }` |
| `SecurityConfig.java` | Permits `/api/auth/**`; all others require authentication; stateless session; adds `JwtAuthenticationFilter` |

#### Frontend Deliverables
| File | Description |
|------|-------------|
| `login.component.ts` | Reactive form — email, password; calls `authService.login()` → saves session → navigates to dashboard |
| `auth.service.ts` | `login()`, `saveSession()` (stores in `sessionStorage`), `getToken()`, `isLoggedIn()`, `logout()` |
| `auth.guard.ts` | `CanActivateFn` — checks `isLoggedIn()`, redirects to `/login` if false |
| `auth.interceptor.ts` | Clones every request and adds `Authorization: Bearer <token>` header |
| `app.config.ts` | Registers `[tokenExpiryInterceptor, authInterceptor]` globally |

#### JWT Payload Structure
```json
{
  "sub": "user@email.com",
  "iat": 1234567890,
  "exp": 1234654290
}
```

#### Session Storage Keys
| Key | Value |
|-----|-------|
| `nb360_token` | Raw JWT (no `Bearer ` prefix) |
| `nb360_email` | User email |
| `nb360_role` | Role string — `ADMIN` or `CUSTOMER` (normalized, no `ROLE_` prefix) |

---

### Day 4 — RBAC & Profile Management
**Status: ✅ Complete**

#### Objectives
Add method-level authorization with `@PreAuthorize`, implement profile GET/PUT endpoints, and build a profile UI component.

#### Backend Deliverables
| File | Description |
|------|-------------|
| `SecurityConfig.java` | `@EnableMethodSecurity` enabled; URL matchers for `/api/admin/**` → `hasRole("ADMIN")` |
| `UserController.GET /api/users/me` | `@PreAuthorize("isAuthenticated()")` — returns `UserResponse` for logged-in user |
| `UserController.PUT /api/users/me` | Updates `fullName`; returns updated `UserResponse` |
| `UserController.GET /api/users/{userId}` | `@PreAuthorize("hasRole('ADMIN')")` — admin-only lookup by ID |
| `AdminController.GET /api/admin/users` | `@PreAuthorize("hasRole('ADMIN')")` — list all users |
| `UpdateProfileRequest.java` | DTO — `fullName` field |

#### Frontend Deliverables
| File | Description |
|------|-------------|
| `user-api.service.ts` | `getProfile()`, `updateProfile()`, `getAllUsers()` |
| `profile.component.ts` | Displays user data; edit fullName; role-based conditional admin section |
| `role.guard.ts` | `CanActivateFn` — reads `data.roles[]` from route, normalizes role, compares; logs out on mismatch |
| `app.routes.ts` | `/profile` route with `authGuard` |

#### Route Guards
```typescript
// Role-protected route example
{
  path: 'dashboard/admin',
  component: AdminDashboardComponent,
  canActivate: [authGuard, roleGuard],
  data: { roles: ['ADMIN'] }
}
```

---

### Day 5 — Account Module Backend
**Status: ✅ Complete**

#### Objectives
Build the account creation, listing, and ownership-enforced backend APIs.

#### Backend Deliverables
| File | Description |
|------|-------------|
| `CreateAccountRequest.java` | DTO — `accountType` (SAVING / CURRENT) |
| `AccountResponse.java` | DTO — id, accountNumber, accountType, balance |
| `AccountRepository.java` | `findByUser()`, `existsByAccountNumber()` |
| `AccountService.createAccount()` | Gets authenticated user from `SecurityContextHolder` → generates unique `NB<18hex>` account number → saves |
| `AccountService.getMyAccounts()` | Returns accounts belonging to authenticated user only |
| `AccountService.getMyAccountById()` | Ownership check → 403 if account belongs to different user |
| `AccountController.POST /api/accounts` | Creates account — 201 Created |
| `AccountController.GET /api/accounts` | Lists accounts for authenticated user |
| `AccountController.GET /api/accounts/{id}` | Gets single account (ownership enforced) |

#### Account Number Generation
```java
"NB" + UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 18)
// e.g. NB3A9F2C1B4D7E8A2F1C
```

#### Security Notes
- `AccountService` extracts the authenticated principal from `SecurityContextHolder.getContext().getAuthentication().getName()` (the email stored in JWT `sub`)
- Ownership mismatch returns `403 Forbidden`

---

### Day 6 — Account Module Frontend
**Status: ✅ Complete**

#### Objectives
Build the full account management UI with a KYC-style creation form, account list, and detail view.

#### Frontend Deliverables
| File | Description |
|------|-------------|
| `account.component.ts` | Loads accounts; reactive form with 7 fields (accountType, fullName, dob, mobile, email, aadhaar, pan) |
| `account.component.html` | Account type selection, KYC form, account list/cards |
| `account.component.css` | Card grid, form styling |
| `user-api.service.ts` | `createAccount()`, `getMyAccounts()`, `getAccountById()` added |

#### Form Fields & Validation
| Field | Validator |
|-------|-----------|
| `accountType` | Required — `SAVING` / `CURRENT` |
| `fullName` | Required, min 3 chars (pre-filled from profile, disabled) |
| `dob` | Required (date input) |
| `mobile` | Required, pattern `/^[6-9]\d{9}$/` (Indian mobile) |
| `email` | Required, valid email (pre-filled from session, disabled) |
| `aadhaar` | Required, pattern `/^\d{12}$/` (12 digits) |
| `pan` | Required, pattern `/^[A-Z]{5}[0-9]{4}[A-Z]$/` |

#### Route
```typescript
{ path: 'accounts', component: AccountComponent, canActivate: [authGuard] }
```

---

### Day 7 — Transaction Engine
**Status: ✅ Complete**

#### Objectives
Implement deposit, withdrawal, and transaction history APIs using `@Transactional` for atomicity. Each transaction records `balance_after`.

#### Backend Deliverables
| File | Description |
|------|-------------|
| `TransactionRequest.java` | DTO — accountId (Long), amount (BigDecimal), description (String) |
| `TransactionResponse.java` | DTO — id, accountId, accountNumber, type, amount, description, balanceAfter, transactionDate |
| `TransactionRepository.java` | `findByAccountIdOrderByTransactionDateDesc(accountId)` |
| `TransactionService.deposit()` | `@Transactional` — adds amount to balance → saves account → creates CREDIT transaction with `balanceAfter` |
| `TransactionService.withdraw()` | `@Transactional` — validates sufficient balance (`400 Insufficient balance`) → subtracts → creates DEBIT transaction |
| `TransactionService.getTransactionHistory()` | Verifies account ownership → returns list ordered by date DESC |
| `TransactionController.POST /api/transactions/deposit` | Calls `deposit()` — 200 OK with `TransactionResponse` |
| `TransactionController.POST /api/transactions/withdraw` | Calls `withdraw()` — 200 OK; 400 on insufficient balance |
| `TransactionController.GET /api/transactions/{accountId}` | Returns transaction history list |

#### Atomicity
`@Transactional` ensures that if saving the `Transaction` record fails, the balance update on `Account` is also rolled back (and vice versa).

---

### Day 8 — Dashboard (Customer & Admin)
**Status: ✅ Complete**

#### Objectives
Build separate dashboards for customers (account list + creation + real-time balance) and admins (all-accounts table with search/filter).

#### New Backend Endpoint
| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/accounts/admin/all` | GET | Authenticated | Returns all accounts in the system |

#### New Frontend Services
| File | Description |
|------|-------------|
| `account-balance.service.ts` | Singleton service with `EventEmitter<AccountData>` — `notifyBalanceChange(account)` broadcast |
| `user-api.service.ts` | `getAllAccounts()` added — calls `GET /api/accounts/admin/all` with explicit `Authorization` header |

#### Customer Dashboard (`/dashboard/customer`)
| Feature | Implementation |
|---------|---------------|
| Account cards grid | `getMyAccounts()` on init, displayed as cards with badge colors |
| Account creation form | Inline form (SAVING / CURRENT) → calls `createAccount()` → `accounts.unshift(created)` |
| Real-time balance | Subscribes to `AccountBalanceService.balanceUpdated` → calls `updateAccountInList()` |
| Profile name | Loaded via `getProfile()` on init |

#### Admin Dashboard (`/dashboard/admin`)
| Feature | Implementation |
|---------|---------------|
| All accounts table | `getAllAccounts()` on init; table with accountNumber, type, balance, status |
| Search box | Filters by account number substring (case-insensitive) |
| Account type filter | Dropdown — All / Saving / Current |
| Stats row | Total accounts, showing count, total balance (INR formatted) |
| 401 auto-logout | On 401 response: `authService.logout()` → `router.navigate(['/login'])` |

#### Real-Time Balance Flow
```
TransactionComponent → deposit/withdraw
        ↓
userApi.getAccountById(id)
        ↓
balanceService.notifyBalanceChange(updatedAccount)
        ↓
CustomerDashboardComponent (subscribed via takeUntil(destroy$))
        ↓
updateAccountInList(updatedAccount) → immediate UI update
```

---

### Day 9 — Transaction UI (Grid, Filter, Sort)
**Status: ✅ Complete**

#### Objectives
Upgrade the transaction history view from a simple list to a proper data table with client-side filtering and sorting, colour-coded transaction types.

#### Frontend Changes

**`transaction.component.ts` — New State & Logic**
```typescript
// State
activeFilter: 'ALL' | 'CREDIT' | 'DEBIT' = 'ALL';
sortAsc = false;

// Computed getter — no backend call needed
get filteredHistory(): TransactionData[] {
  let result = this.activeFilter === 'ALL'
    ? [...this.history]
    : this.history.filter(t => t.type === this.activeFilter);

  result.sort((a, b) => {
    const diff = new Date(a.transactionDate).getTime() - new Date(b.transactionDate).getTime();
    return this.sortAsc ? diff : -diff;
  });
  return result;
}

setFilter(filter: 'ALL' | 'CREDIT' | 'DEBIT'): void { this.activeFilter = filter; }
toggleSort(): void { this.sortAsc = !this.sortAsc; }
```

**`transaction.component.html` — Table Structure**
```html
<!-- Filter buttons -->
<div class="filter-btns">
  <button [class.active]="activeFilter === 'ALL'"   (click)="setFilter('ALL')">All</button>
  <button [class.active]="activeFilter === 'CREDIT'" (click)="setFilter('CREDIT')">Credit</button>
  <button [class.active]="activeFilter === 'DEBIT'"  (click)="setFilter('DEBIT')">Debit</button>
</div>
<button (click)="toggleSort()">Date {{ sortAsc ? '▲ Asc' : '▼ Desc' }}</button>

<!-- 5-column table -->
<table class="txn-table">
  <thead><tr>
    <th>Date</th><th>Type</th><th>Amount</th><th>Description</th><th>Balance After</th>
  </tr></thead>
  <tbody>
    <tr *ngFor="let t of filteredHistory"
        [class.row-credit]="t.type === 'CREDIT'"
        [class.row-debit]="t.type === 'DEBIT'">
      ...
    </tr>
  </tbody>
</table>
```

**`transaction.component.css` — Colour Specification**
| Class | Colour |
|-------|--------|
| `.credit` (amount text) | `#2E7D32` (dark green) |
| `.debit` (amount text) | `#C62828` (dark red) |
| `.type-badge.credit` | background `#e8f5e9`, text `#2E7D32` |
| `.type-badge.debit` | background `#ffebee`, text `#C62828` |
| `.row-credit` | `border-left: 3px solid #2E7D32` |
| `.row-debit` | `border-left: 3px solid #C62828` |
| `.filter-btns button.active` | `background: #0f172a`, text white |

---

### Day 10 — Testing & Quality
**Status: ✅ Complete (Unit + Integration tests implemented)**

#### Objectives
Write unit tests for service layer and controller, DTO validation tests, and ensure zero compile/lint errors.

#### Backend Test Files

**`UserServiceTest.java`** — `@ExtendWith(MockitoExtension.class)`
| Test | Description |
|------|-------------|
| `testRegisterSuccessfully()` | Mocks repo + encoder; verifies `UserResponse` returned; verifies `save()` called once |
| `testRegisterWithDuplicateEmail()` | Mocks `existsByEmail = true`; verifies `IllegalArgumentException` thrown |
| `testRegisterWithPasswordMismatch()` | Mismatched passwords; verifies exception |
| `testLoginSuccessfully()` | Mocks user + password match; verifies `LoginResponse` with token |
| `testLoginWithInvalidCredentials()` | Wrong password; verifies 401 exception |

**`AuthControllerTest.java`** — `MockMvc` standalone setup
| Test | Description |
|------|-------------|
| `testRegisterSuccessfully()` | POST `/api/auth/register` → asserts 201, `$.email`, `$.fullName` in response |
| `testRegisterWithDuplicateEmail()` | Service throws `IllegalArgumentException("Email already registered")` → asserts 409 |
| `testRegisterWithPasswordMismatch()` | Service throws `IllegalArgumentException("Passwords do not match")` → asserts 400 |
| `testLoginSuccessfully()` | POST `/api/auth/login` → asserts 200, `$.token` not null |
| `testLoginWithInvalidCredentials()` | Service throws → asserts 401 |

**`RegisterRequestValidationTest.java`** — `@SpringBootTest` + `Validator`
| Test | Validates |
|------|-----------|
| `testValidRegistrationRequest()` | Zero violations on valid input |
| `testBlankFullName()` | Violation on `fullName = ""` |
| `testShortFullName()` | Violation on `fullName = "Jo"` (< 3 chars) |
| `testInvalidEmail()` | Violation on `email = "invalid-email"` |
| `testWeakPassword()` | Violation on password missing complexity requirements |

**`NeoBank360appApplicationTests.java`**
- `contextLoads()` — Spring context integration test

#### Frontend Quality
| Check | Result |
|-------|--------|
| `ng build` | ✅ Pass — no TypeScript errors |
| `get_errors` (VS Code) | ✅ No errors in any `.ts` or `.html` file |
| Standalone components | ✅ All components are standalone (no NgModule) |
| RxJS `takeUntil(destroy$)` | ✅ Used in dashboard components to prevent memory leaks |
| `OnDestroy` lifecycle | ✅ Implemented in `CustomerDashboardComponent` and `AdminDashboardComponent` |

---

## 6. API Reference

### Authentication (`/api/auth`)

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|-------------|---------|
| POST | `/api/auth/register` | None | `RegisterRequest` | 201 `UserResponse` |
| POST | `/api/auth/login` | None | `LoginRequest` | 200 `LoginResponse` |

### User Profile (`/api/users`)

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|-------------|---------|
| GET | `/api/users/me` | Authenticated | — | 200 `UserResponse` |
| PUT | `/api/users/me` | Authenticated | `UpdateProfileRequest` | 200 `UserResponse` |
| GET | `/api/users/{userId}` | Admin only | — | 200 `UserResponse` |

### Admin (`/api/admin`)

| Method | Endpoint | Auth | Response |
|--------|----------|------|---------|
| GET | `/api/admin/users` | Admin only | 200 `List<UserResponse>` |

### Accounts (`/api/accounts`)

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|-------------|---------|
| POST | `/api/accounts` | Authenticated | `CreateAccountRequest` | 201 `AccountResponse` |
| GET | `/api/accounts` | Authenticated | — | 200 `List<AccountResponse>` |
| GET | `/api/accounts/{id}` | Authenticated (owner) | — | 200 `AccountResponse` |
| GET | `/api/accounts/admin/all` | Authenticated | — | 200 `List<AccountResponse>` |

### Transactions (`/api/transactions`)

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|-------------|---------|
| POST | `/api/transactions/deposit` | Authenticated | `TransactionRequest` | 200 `TransactionResponse` |
| POST | `/api/transactions/withdraw` | Authenticated | `TransactionRequest` | 200 `TransactionResponse` |
| GET | `/api/transactions/{accountId}` | Authenticated (owner) | — | 200 `List<TransactionResponse>` |

### Error Responses

| HTTP Status | Meaning |
|-------------|---------|
| 400 | Validation failure or insufficient balance |
| 401 | Missing or invalid JWT |
| 403 | Authenticated but insufficient role / not account owner |
| 404 | Resource not found |
| 409 | Email already registered |
| 500 | Internal server error |

---

## 7. Security Architecture

### JWT Flow
```
Client                          Backend
  │                               │
  ├─ POST /api/auth/login ───────>│
  │   { email, password }         │── UserService.login()
  │                               │   → BCrypt verify
  │                               │   → JwtService.generateToken()
  │<─ 200 { token, role } ────────│
  │                               │
  ├─ GET /api/accounts ──────────>│
  │   Authorization: Bearer <JWT> │── JwtAuthenticationFilter
  │                               │   → extractEmail(token)
  │                               │   → load User from DB
  │                               │   → SimpleGrantedAuthority("ROLE_CUSTOMER")
  │                               │   → SecurityContextHolder.setAuthentication()
  │                               │── AccountService.getMyAccounts()
  │<─ 200 [ accounts ] ───────────│
```

### SecurityConfig Rules (evaluated top-to-bottom)
```java
.requestMatchers("/api/auth/**")               → permitAll()
.requestMatchers("/api/admin/**")              → hasRole("ADMIN")
.requestMatchers("/api/accounts/admin/**")     → authenticated()
.requestMatchers("/api/customer/**")           → hasAnyRole("CUSTOMER","ADMIN")
.requestMatchers("/api/transactions/**")       → authenticated()
.requestMatchers("/api/accounts/**")           → authenticated()
.anyRequest()                                  → authenticated()
```

### CORS Configuration
- Allowed origins: `http://localhost:4200`, `http://localhost:4201`
- Allowed methods: `GET, POST, PUT, DELETE, OPTIONS`
- Allowed headers: `*`
- Credentials: `true`

### Frontend Token Handling
```typescript
// auth.service.ts — saveSession()
const normalizedToken = token.replace(/^Bearer\s+/i, '').trim();  // strip Bearer prefix
const normalizedRole  = role.replace(/^ROLE_/i, '').toUpperCase(); // strip ROLE_ prefix

// auth.interceptor.ts
const token = rawToken.replace(/^Bearer\s+/i, '').trim();
req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });

// auth.service.ts — isLoggedIn()
return !!token && !this.isTokenExpired(token);
// isTokenExpired() decodes JWT payload, checks exp * 1000 < Date.now()
```

---

## 8. Running the Application

### Prerequisites
- Java 17+
- Node.js 18+ and npm
- MySQL 8.0+
- Angular CLI (`npm install -g @angular/cli`)

### Backend Setup
```bash
# 1. Create MySQL database
CREATE DATABASE neobank360;

# 2. Update application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/neobank360
spring.datasource.username=<your_user>
spring.datasource.password=<your_password>
spring.jpa.hibernate.ddl-auto=update

# 3. Start backend (from Backend/NeoBank360app/)
./mvnw spring-boot:run
# Server starts on http://localhost:8080
# Admin user seeded: admin@neobank.com / admin123
```

### Frontend Setup
```bash
# From Frontend/NeoBank360_UI/
npm install
ng serve --open
# App starts on http://localhost:4200
```

### Test Accounts
| Role | Email | Password |
|------|-------|----------|
| Admin | `admin@neobank.com` | `admin123` |
| Customer | Register via `/register` | Your choice |

### Quick API Test (PowerShell)
```powershell
# Login
$body   = @{ email = 'admin@neobank.com'; password = 'admin123' } | ConvertTo-Json
$login  = Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/auth/login' `
            -ContentType 'application/json' -Body $body
$headers = @{ Authorization = "Bearer $($login.token)" }

# Get all accounts (admin)
Invoke-RestMethod -Method Get -Uri 'http://localhost:8080/api/accounts/admin/all' -Headers $headers
```

---

## 9. Completion Status Summary

| Day | Feature | Backend | Frontend | Tests | Status |
|-----|---------|---------|----------|-------|--------|
| 1 | Environment & Database Setup | ✅ | ✅ | — | **✅ Complete** |
| 2 | User Registration | ✅ | ✅ | ✅ | **✅ Complete** |
| 3 | Login + JWT + Guards + Interceptor | ✅ | ✅ | ✅ | **✅ Complete** |
| 4 | RBAC + `@PreAuthorize` + Profile GET/PUT | ✅ | ✅ | — | **✅ Complete** |
| 5 | Account Module Backend (CRUD + ownership) | ✅ | — | — | **✅ Complete** |
| 6 | Account Module Frontend (KYC form + list) | — | ✅ | — | **✅ Complete** |
| 7 | Transaction Engine (`@Transactional`, `balance_after`) | ✅ | ✅ | — | **✅ Complete** |
| 8 | Dashboard (Customer + Admin, real-time balance) | ✅ | ✅ | — | **✅ Complete** |
| 9 | Transaction UI (table, filter, sort, colour coding) | — | ✅ | — | **✅ Complete** |
| 10 | Testing & Quality (unit + integration + build check) | ✅ | ✅ | ✅ | **✅ Complete** |

### Overall Score: 10 / 10 Days Complete ✅

---

### Files Created / Modified Per Day (Quick Reference)

| Day | Backend Files | Frontend Files |
|-----|--------------|----------------|
| 1 | `User.java`, `Account.java`, `Transaction.java`, `pom.xml` | `angular.json`, `tsconfig.json`, `app.routes.ts` |
| 2 | `RegisterRequest.java`, `UserResponse.java`, `UserService.register()`, `AuthController.register()` | `register.component.*`, `auth.service.ts` |
| 3 | `JwtService.java`, `JwtAuthenticationFilter.java`, `UserService.login()`, `SecurityConfig.java` | `login.component.*`, `auth.guard.ts`, `auth.interceptor.ts`, `app.config.ts` |
| 4 | `UserController.java`, `AdminController.java`, `UpdateProfileRequest.java`, `SecurityConfig` (RBAC) | `profile.component.*`, `role.guard.ts`, `user-api.service.ts` |
| 5 | `AccountController.java`, `AccountService.java`, `AccountResponse.java`, `CreateAccountRequest.java` | — |
| 6 | — | `account.component.*` (KYC form + account list) |
| 7 | `TransactionController.java`, `TransactionService.java`, `TransactionRequest/Response.java` | `transaction.component.*` (initial), `user-api.service.ts` (deposit/withdraw/history) |
| 8 | `AccountController.GET /admin/all`, `AccountService.getAllAccounts()` | `account-balance.service.ts`, `customer-dashboard.component.*`, `admin-dashboard.component.*` |
| 9 | — | `transaction.component.ts` (filter/sort), `transaction.component.html` (table), `transaction.component.css` (colours) |
| 10 | `UserServiceTest.java`, `AuthControllerTest.java`, `RegisterRequestValidationTest.java` | `ng build` (zero TypeScript errors) |

---

*Generated April 22, 2026 — NeoBank360 v1.0*
