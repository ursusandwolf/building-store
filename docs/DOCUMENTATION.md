# Documentation

## API Endpoints
### Authentication
- `POST /api/auth/register`: Register a new customer (returns `UserResponse`).
- `POST /api/auth/login`: Authenticate and get JWT token (returns `AuthResponse`).

### Public
- `GET /api/public/health`: Returns application status.
- `GET /api/public/version`: Returns project version.
- `GET /api/catalog/products`: Returns a list of all active products.
- `GET /api/catalog/products/{id}`: Returns details of a specific active product by ID.
- `GET /api/catalog/products/{id}/packages`: Returns a list of packages for a specific active product.
- `GET /api/catalog/products/{id}/price`: Returns the active price for a specific product.

### Private
- `GET /api/private/hello`: Returns a greeting (requires JWT Bearer token).

### Admin
- `GET /api/admin/users`: Returns a list of all users with their roles (requires `ROLE_ADMIN` role).
- `POST /api/admin/products`: Creates a new product (requires `ROLE_ADMIN` role, returns `ProductResponse`).
- `PUT /api/admin/products/{id}`: Updates an existing product by ID (requires `ROLE_ADMIN` role, returns `ProductResponse`).
- `POST /api/admin/products/{id}/packages`: Adds a new package to a product (requires `ROLE_ADMIN` role, returns `ProductPackageResponse`).
- `GET /api/admin/products/{id}/packages`: Returns all packages for a specific product (requires `ROLE_ADMIN` role).
- `POST /api/admin/warehouses`: Creates a new warehouse (requires `ROLE_ADMIN` role).
- `PUT /api/admin/warehouses/{id}`: Updates a warehouse (requires `ROLE_ADMIN` role).
- `GET /api/admin/warehouses`: Returns all warehouses (requires `ROLE_ADMIN`, `ROLE_WAREHOUSE_MANAGER`, or `ROLE_AUDITOR`).
- `GET /api/admin/warehouses/{id}`: Returns a warehouse by ID (requires `ROLE_ADMIN`, `ROLE_WAREHOUSE_MANAGER`, or `ROLE_AUDITOR`).
- `POST /api/admin/stock-adjustments`: Adjusts stock levels (requires `ROLE_ADMIN` or `ROLE_WAREHOUSE_MANAGER`).
- `POST /api/admin/suppliers`: Creates a new supplier (requires `ROLE_ADMIN` role).
- `GET /api/admin/suppliers`: Returns all suppliers (requires `ROLE_ADMIN` or `ROLE_PURCHASING_MANAGER`).
- `POST /api/admin/purchase-orders`: Creates a new purchase order (requires `ROLE_ADMIN` or `ROLE_PURCHASING_MANAGER`).
- `GET /api/admin/purchase-orders`: Returns all purchase orders (requires `ROLE_ADMIN` or `ROLE_PURCHASING_MANAGER`).
- `POST /api/admin/purchase-orders/{id}/goods-receipts`: Processes goods receipt for an order (requires `ROLE_ADMIN` or `ROLE_WAREHOUSE_MANAGER`).
- `POST /api/admin/price-lists`: Creates a new price list (requires `ROLE_ADMIN` role).
- `POST /api/admin/price-lists/{id}/items`: Adds an item to a price list (requires `ROLE_ADMIN` role).

### Employee
- `GET /api/employees/me`: Returns details of the currently authenticated employee (requires any employee role).

## Security
- **Authentication**: Stateless JWT.
- **Authorization**: Bearer token in `Authorization` header.
- **Role-based Security**: Layered security using requestMatchers in `SecurityFilterChain` for URL patterns and `@EnableMethodSecurity` / `@PreAuthorize` on controller methods.
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
