# Documentation

## API Endpoints
- `GET /api/public/health`: Returns application status.
- `GET /api/public/version`: Returns project version.

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
