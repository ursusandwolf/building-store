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
