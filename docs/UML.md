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
├── security
│   ├── config
│   │   └── SecurityConfig
│   └── service
│       └── CustomUserDetailsService
└── user
    ├── controller
    │   └── AuthController
    ├── dto
    │   └── RegisterRequest
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
