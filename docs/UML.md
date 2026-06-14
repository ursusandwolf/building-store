# UML Diagrams

## Iteration 0: Package Structure
```text
com.example.buildstore
├── BuildStoreApplication
├── common
│   ├── api
│   │   └── HealthController
│   ├── controller
│   │   └── PrivateHelloController
│   └── exception
│       ├── ErrorResponse
│       └── GlobalExceptionHandler
├── security
│   ├── config
│   │   └── SecurityConfig
│   └── service
│       └── CustomUserDetailsService
└── user
    ├── model
    │   ├── Role
    │   ├── RoleName
    │   ├── User
    │   └── UserStatus
    └── repository
        ├── RoleRepository
        └── UserRepository
```
