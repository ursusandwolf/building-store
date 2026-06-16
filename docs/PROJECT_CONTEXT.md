# Project Context: Building Materials Store

## Project Overview
Educational project building a backend for a building materials store with Spring Boot, Spring Security, and complex business logic (warehouse, accounting, etc.).

## General Rules
- В конце каждой итерации ИИ-агент должен предоставлять список вопросов для самопроверки и понимания кода.

## Current Status
- **Iteration 0: Project Skeleton** - COMPLETED.
- **Iteration 1: Security Setup** - COMPLETED.
- Iteration 2: DB-Backed Authentication - COMPLETED.
- Iteration 3: Customer Registration - COMPLETED.
- Iteration 4: JWT Authentication - COMPLETED.
- Iteration 5: Method security and employee roles - COMPLETED.
- Package refactored to `com.buildstore`.
- `User` entity renamed to `AppUser`.
- Customer registration implemented with DTO validation.
- JWT authentication implemented (stateless).
- Enabled method security with `@EnableMethodSecurity`.
- Secured admin `/api/admin/**` and employee `/api/employees/**` endpoints with `@PreAuthorize`.
- All tests passing.

## Pending Items
- Iteration 6: Product catalog.
- Iteration 7: Units and product packages.
- Iteration 8: Warehouses.
- Iteration 9: StockItem and stock movement.
...
