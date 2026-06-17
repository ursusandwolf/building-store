# Changelog

## [0.21.0-SNAPSHOT] - 2026-06-17
### Added
- Project User Manual created in `docs/USER_MANUAL.md`.
- Infrastructure setup with `Dockerfile` and `docker-compose.yml`.
### Changed
- Finalized system documentation and cleaned up project context.

## [0.20.0-SNAPSHOT] - 2026-06-17
### Added
- Report module implementation (Iteration 20).
- `ReportService` and `ReportController` to expose basic sales and inventory reports.

## [0.19.0-SNAPSHOT] - 2026-06-17
### Added
- ReturnOrder module implementation (Iteration 19).
- `ReturnOrder`, `ReturnOrderLine` entities, repository, service and controller.
- Liquibase migration `v019-create-return-orders.xml`.

## [0.18.0-SNAPSHOT] - 2026-06-17
### Added
- Delivery module implementation (Iteration 18).
- `Shipment` and `ShipmentLine` JPA entities.
- `ShipmentRepository`, `DeliveryService` and `DeliveryController`.
- Liquibase migration `v018-create-shipments.xml`.
- Integrated stock consumption into `InventoryService` and `ReservationService` to support delivery.

## [0.17.0-SNAPSHOT] - 2026-06-17
### Added
- Dedicated SYSTEM user implementation (`v017-create-system-user.xml` and `SystemUserProvider`).
### Changed
- Propagated SYSTEM user to `SalesOrderService` and `PaymentService` for audit integrity.
- Removed hardcoded "SYSTEM" strings from core services.

## [0.16.0-SNAPSHOT] - 2026-06-17
### Added
- Finalized Accounting module with Audit integration (Iteration 16).
- Integrated `AuditService` into `AccountingService` for `Invoice` creation and `AccountingEntry` recording.

## [0.15.0-SNAPSHOT] - 2026-06-16
### Added
- Audit module implementation (Iteration 15).
- `AuditEvent` entity, repository, and `AuditService`.
- Liquibase migration `v015-create-audit-events.xml`.
- Integrated `AuditService` into `SalesOrderService`.
### Changed
- Refactored JPA entities to remove `@Builder` and `@AllArgsConstructor` (following best practices).
- Updated tests to use explicit instantiation via constructors and setters.
- Refactored `SalesOrderService` to persist `SalesOrderLine` before `StockReservation` creation.

## [0.14.0-SNAPSHOT] - 2026-06-16
### Added
- Sales Order Draft implementation (Iteration 14).
...

- Discount policy implementation (Iteration 13).
- ...
### Added
- Price lists implementation (Iteration 12).
- `PriceList` and `PriceListItem` entities.
- `PriceListService` for managing prices and `getActivePrice` logic (active, valid time range).
- `PriceListController` for admin management of prices and public price lookups.
- Liquibase migration `v008-create-price-lists.xml`.
- Integration tests in `PricingTests`.

## [0.11.0-SNAPSHOT] - 2026-06-16
### Added
- Goods receipt implementation (Iteration 11).
- `GoodsReceiptService` for processing order receipts and updating inventory.
- `AdminGoodsReceiptController` endpoint `POST /api/admin/purchase-orders/{id}/goods-receipts`.
- Idempotency support for goods receipts using a unique key to prevent duplicate stock movements.
- Automated update of `PurchaseOrder` status to `COMPLETED` upon full receipt.
- Integration tests in `GoodsReceiptTests`.

## [0.10.0-SNAPSHOT] - 2026-06-16
### Added
- Supplier and Purchase Order management implementation (Iteration 10).
- `Supplier` entity for managing procurement sources.
- `PurchaseOrder` and `PurchaseOrderLine` entities for purchase tracking.
- Liquibase migration `v006-create-suppliers.xml` and `v007-create-purchase-orders.xml`.
- Admin endpoints for managing suppliers and purchase orders.
- Role-based access control for purchasing functionality (Admin, Purchasing Manager).
- Integration tests in `PurchaseOrderTests`.

## [0.9.0-SNAPSHOT] - 2026-06-16
### Added
- Inventory management implementation (Iteration 9).
- `StockItem` JPA entity for tracking inventory levels per product and warehouse.
- `StockMovement` entity for immutable history of inventory changes.
- `StockMovementType` enum.
- `StockItemRepository` with pessimistic locking for concurrency safety.
- `InventoryService` for stock adjustments (including negative stock prevention).
- `AdminInventoryController` for stock adjustments.
- `InventoryTests` for verifying adjustment workflow and concurrency safety.
- Liquibase migration `v005-create-inventory.xml`.

## [0.8.0-SNAPSHOT] - 2026-06-16
### Added
- Warehouse management implementation (Iteration 8).
- `Warehouse` JPA entity with code, name, and status.
- `WarehouseRepository`, `WarehouseService`, and `AdminWarehouseController`.
- Liquibase migration `v004-create-warehouses.xml`.
- Role-based access control for warehouses:
    - Admin: Full CRUD access.
    - Warehouse Manager & Auditor: Read-only access.
    - Customer: No access.
- `WarehouseTests` for verification.
- Updated `SecurityConfig` to allow Warehouse Manager and Auditor access to specific admin endpoints.

## [0.7.0-SNAPSHOT] - 2026-06-16
### Added
- Units and Product Packages implementation (Iteration 7).
- `ProductPackage` JPA entity and `ProductPackageRepository`.
- `UnitOfMeasure` enum with common measurement units.
- Liquibase migration `v003-create-product-packages.xml` for package storage.
- `ProductPackageRequest` and `ProductPackageResponse` DTO records.
- Service logic for managing packages, including default package enforcement (only one default for sale/purchase).
- `AdminProductController` endpoints: `POST /api/admin/products/{id}/packages` and `GET /api/admin/products/{id}/packages`.
- `CatalogController` endpoint: `GET /api/catalog/products/{id}/packages` for viewing packages of active products.
- Updated `ProductCatalogTests` with package workflow validation and duplicate barcode checks.

## [0.6.0-SNAPSHOT] - 2026-06-16
### Added
- Product Catalog implementation (Iteration 6).
- `Product` and `ProductCategory` JPA entities with lazy relationships, optimistic locking (`@Version`), and index configurations.
- Liquibase migration `v002-create-product-and-category.xml` defining schema and seeding initial categories.
- `ProductRepository` and `ProductCategoryRepository` with `@EntityGraph` to prevent N+1 queries.
- `ProductRequest` and `ProductResponse` DTO records with bean validations.
- `ProductService` implementing SKU uniqueness checks and active status filtering.
- `CatalogController` exposing public `/api/catalog/products` and `/api/catalog/products/{id}`.
- `AdminProductController` exposing administrative `/api/admin/products` and `/api/admin/products/{id}` (restricted to `ROLE_ADMIN`).
- `ProductCatalogTests` validating SKU uniqueness, catalog visibility, and correct DTO mapping.
- Custom `ResourceNotFoundException` mapped to 404 HTTP status code.

## [0.5.0-SNAPSHOT] - 2026-06-16
### Added
- Method Security and Employee Roles (Iteration 5).
- Enabled method security with `@EnableMethodSecurity`.
- Added request-level and method-level (using `@PreAuthorize`) authorization for endpoints.
- `AdminUserController` with `GET /api/admin/users` (restricted to `ROLE_ADMIN`).
- `EmployeeController` with `GET /api/employees/me` (restricted to employee roles: sales manager, warehouse manager, purchasing manager, accountant, auditor, admin).
- Enhanced `UserResponse` DTO to return user's roles.
- `MethodSecurityTests` verifying proper role isolation (e.g. customer vs employee, accountant vs admin).

## [0.0.1-SNAPSHOT] - 2026-06-14
### Added
- Initial project skeleton (Iteration 0).
- Maven `pom.xml` with Spring Boot 3.2.5.
- `HealthController` with `/api/public/health`.
- `ErrorResponse` and `GlobalExceptionHandler`.
- PostgreSQL and Liquibase configuration.
- H2 support for tests.
- Context test.

## [0.1.0-SNAPSHOT] - 2026-06-14
### Added
- Spring Security integration (Iteration 1).
- `SecurityConfig` with `SecurityFilterChain`.
- In-memory authentication with `UserDetailsService`.
- `PrivateHelloController` for testing protected endpoints.
- Security integration tests.

## [0.2.0-SNAPSHOT] - 2026-06-14
### Added
- DB-backed authentication (Iteration 2).
- `User` and `Role` entities with JPA Auditing.
- `UserStatus` and `RoleName` enums.
- Liquibase migration for users, roles, and user_roles tables.
- `CustomUserDetailsService` for loading users from database.
- BCrypt password encoding.
- `DatabaseAuthenticationTests`.

## [0.3.0-SNAPSHOT] - 2026-06-14
### Added
- Customer registration implementation (Iteration 3).
- `AppUser` entity (renamed from `User`).
- `Customer` entity and repository.
- `AuthController` for registration.
- Integration tests for registration.

### Changed
- Package refactored from `com.example.buildstore` to `com.buildstore`.
- Unified error handling for `IllegalArgumentException` and `DataIntegrityViolationException`.

## [0.4.0-SNAPSHOT] - 2026-06-15
### Added
- JWT Authentication (Iteration 4).
- JJWT dependencies for manual JWT implementation.
- `JwtService` for token generation, extraction, and validation.
- `JwtAuthenticationFilter` for stateless authentication per request.
- `JwtAuthenticationEntryPoint` for consistent 401 responses.
- `LoginRequest` and `AuthResponse` DTOs.
- `POST /api/auth/login` endpoint.
- Handling for `BadCredentialsException` and `AccessDeniedException` in `GlobalExceptionHandler`.
- `JwtAuthenticationTests`.

### Changed
- Switched security configuration from HTTP Basic to stateless JWT.
- Updated `SecurityConfig` to include JWT filter and entry point.
- Hardened JWT implementation: optimized login (no double DB lookup), added account status checks, cached signing key, and converted DTOs to Java Records.
- Removed obsolete `DatabaseAuthenticationTests`.
