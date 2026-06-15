# Documentation

## API Endpoints
### Authentication
- `POST /api/auth/register`: Register a new customer (returns `UserResponse`).
- `POST /api/auth/login`: Authenticate and get JWT token (returns `AuthResponse`).

### Public
- `GET /api/public/health`: Returns application status.
- `GET /api/public/version`: Returns project version.

### Private
- `GET /api/private/hello`: Returns a greeting (requires JWT Bearer token).

## Security
- **Authentication**: Stateless JWT.
- **Authorization**: Bearer token in `Authorization` header.
- **Password Storage**: BCrypt hashing.
- **Account Checks**: User status (ACTIVE, SUSPENDED, CLOSED, etc.) is checked on every request.

## Configuration
- `application.yml`: Main configuration.
- `application-test.yml`: Test-specific overrides (using H2).

## Error Format
All errors follow the `ErrorResponse` structure:
```json
{
  "timestamp": "...",
  "status": 500,
  "code": "INTERNAL_SERVER_ERROR",
  "message": "...",
  "path": "...",
  "traceId": "..."
}
```
