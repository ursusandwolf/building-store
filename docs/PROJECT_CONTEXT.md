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
- Iteration 6: Product catalog - COMPLETED.
- Iteration 7: Units and product packages - COMPLETED.
- Iteration 8: Warehouses - COMPLETED.
- **Iteration 9: StockItem and stock movement** - COMPLETED.
- Package refactored to `com.buildstore`.
- `User` entity renamed to `AppUser`.
- Customer registration implemented with DTO validation.
- JWT authentication implemented (stateless).
- Enabled method security with `@EnableMethodSecurity`.
- Secured admin `/api/admin/**` and employee `/api/employees/**` endpoints with `@PreAuthorize`.
- Product and product category tables defined with Liquibase, and seed categories populated.
- Pre-fetching with `@EntityGraph` used to prevent N+1 query problems in ProductRepository.
- Catalog endpoints implemented with proper validations and access controls.
- Product packages and units implemented with admin and catalog endpoints.
- Proper mapping of boolean fields in product packages (defaultForSale, defaultForPurchase).
- Warehouse management implemented with restricted access (Admin, Warehouse Manager, Auditor).
- Inventory models (StockItem, StockMovement) and service (InventoryService) implemented.
- All tests passing.

## Pending Items
- Iteration 10: Pricing.
- Iteration 11: Goods receipt.
...
