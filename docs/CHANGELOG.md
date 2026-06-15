# Changelog

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
