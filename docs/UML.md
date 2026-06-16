# UML Diagrams

## Iteration 0: Package Structure
```text
com.buildstore
├── BuildStoreApplication
├── common
│   ├── api
│   │   └── HealthController
│   ├── controller
│   │   └── PrivateHelloController
│   └── exception
│       ├── ErrorResponse
│       └── GlobalExceptionHandler
├── customer
│   ├── model
│   │   └── Customer
│   └── repository
│       └── CustomerRepository
├── employee
│   └── controller
│       └── EmployeeController
├── product
│   ├── controller
│   │   ├── CatalogController
│   │   └── AdminProductController
│   ├── dto
│   │   ├── ProductRequest
│   │   └── ProductResponse
│   ├── mapper
│   │   └── ProductMapper
│   ├── model
│   │   ├── Product
│   │   ├── ProductCategory
│   │   ├── ProductStatus
│   │   └── UnitOfMeasure
│   ├── repository
│   │   ├── ProductRepository
│   │   └── ProductCategoryRepository
│   └── service
│       └── ProductService
├── security
│   ├── config
│   │   └── SecurityConfig
│   ├── filter
│   │   └── JwtAuthenticationFilter
│   ├── service
│   │   └── JwtService
│   ├── CustomUserDetailsService
│   └── JwtAuthenticationEntryPoint
└── user
    ├── controller
    │   ├── AuthController
    │   └── AdminUserController
    ├── dto
    │   ├── RegisterRequest
    │   ├── LoginRequest
    │   └── AuthResponse
    ├── model
    │   ├── AppUser
    │   ├── Role
    │   ├── RoleName
    │   └── UserStatus
    ├── repository
    │   ├── RoleRepository
    │   └── UserRepository
    └── service
        └── UserService
```
