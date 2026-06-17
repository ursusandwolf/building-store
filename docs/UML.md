# UML Diagrams

## Iteration 17: Package Structure
```text
com.buildstore
├── BuildStoreApplication
├── accounting
│   ├── controller
│   │   └── InvoiceController
│   ├── dto
│   │   ├── InvoiceLineResponse
│   │   └── InvoiceResponse
│   ├── mapper
│   │   └── InvoiceMapper
│   ├── model
│   │   ├── AccountingEntry
│   │   ├── AccountingEntryType
│   │   ├── Invoice
│   │   ├── InvoiceLine
│   │   └── InvoiceStatus
│   ├── repository
│   │   ├── AccountingEntryRepository
│   │   └── InvoiceRepository
│   └── service
│       └── AccountingService
├── audit
│   ├── model
│   │   └── AuditEvent
│   ├── repository
│   │   └── AuditEventRepository
│   └── service
│       └── AuditService
├── common
│   ├── api
│   │   └── HealthController
│   ├── controller
│   │   └── PrivateHelloController
│   └── exception
│       ├── ErrorResponse
│       ├── GlobalExceptionHandler
│       ├── RegistrationException
│       └── ResourceNotFoundException
├── customer
│   ├── model
│   │   └── Customer
│   └── repository
│       └── CustomerRepository
├── employee
│   └── controller
│       └── EmployeeController
├── inventory
│   ├── controller
│   │   └── AdminInventoryController
│   ├── dto
│   │   ├── StockAdjustmentRequest
│   │   └── StockMovementResponse
│   ├── model
│   │   ├── ReservationStatus
│   │   ├── StockItem
│   │   ├── StockMovement
│   │   ├── StockMovementType
│   │   └── StockReservation
│   ├── repository
│   │   ├── StockItemRepository
│   │   ├── StockMovementRepository
│   │   └── StockReservationRepository
│   └── service
│       ├── InventoryService
│       └── ReservationService
├── order
│   ├── controller
│   │   └── OrderController
│   ├── dto
│   ├── mapper
│   ├── model
│   ├── repository
│   └── service
│       └── SalesOrderService
├── payment
│   ├── controller
│   ├── dto
│   ├── model
│   ├── repository
│   └── service
│       └── PaymentService
├── pricing
│   ├── controller
│   ├── dto
│   ├── model
│   ├── repository
│   └── service
│       └── PriceListService
├── product
│   ├── controller
│   ├── dto
│   ├── mapper
│   ├── model
│   ├── repository
│   └── service
│       └── ProductService
├── purchase
│   ├── controller
│   ├── dto
│   ├── mapper
│   ├── model
│   ├── repository
│   └── service
│       └── PurchaseOrderService
├── security
│   ├── JwtAuthenticationEntryPoint
│   ├── config
│   ├── filter
│   │   └── JwtAuthenticationFilter
│   └── service
│       ├── JwtService
│       └── SystemUserProvider
├── supplier
│   ├── controller
│   ├── dto
│   ├── mapper
│   ├── model
│   ├── repository
│   └── service
│       └── SupplierService
├── user
│   ├── controller
│   ├── dto
│   ├── mapper
│   ├── model
│   ├── repository
│   └── service
│       └── UserService
└── warehouse
    ├── controller
    ├── dto
    ├── mapper
    ├── model
    ├── repository
    └── service
        └── WarehouseService
```
