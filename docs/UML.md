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
│   │   ├── ProductResponse
│   │   ├── ProductPackageRequest
│   │   └── ProductPackageResponse
│   ├── mapper
│   │   └── ProductMapper
│   ├── model
│   │   ├── Product
│   │   ├── ProductCategory
│   │   ├── ProductPackage
│   │   ├── ProductStatus
│   │   └── UnitOfMeasure
│   ├── repository
│   │   ├── ProductRepository
│   │   ├── ProductCategoryRepository
│   │   └── ProductPackageRepository
│   └── service
│       └── ProductService
├── warehouse
│   ├── controller
│   │   └── AdminWarehouseController
│   ├── dto
│   │   ├── WarehouseRequest
│   │   └── WarehouseResponse
│   ├── mapper
│   │   └── WarehouseMapper
│   ├── model
│   │   └── Warehouse
│   ├── repository
│   │   └── WarehouseRepository
│   └── service
│       └── WarehouseService
├── supplier
│   ├── controller
│   │   └── AdminSupplierController
│   ├── dto
│   │   ├── SupplierRequest
│   │   └── SupplierResponse
│   ├── mapper
│   │   └── SupplierMapper
│   ├── model
│   │   └── Supplier
│   ├── repository
│   │   └── SupplierRepository
│   └── service
│       └── SupplierService
├── purchase
│   ├── controller
│   │   ├── AdminPurchaseOrderController
│   │   └── AdminGoodsReceiptController
│   ├── dto
│   │   ├── PurchaseOrderRequest
│   │   ├── PurchaseOrderLineRequest
│   │   ├── PurchaseOrderResponse
│   │   ├── PurchaseOrderLineResponse
│   │   ├── GoodsReceiptRequest
│   │   └── GoodsReceiptLineRequest
│   ├── mapper
│   │   └── PurchaseOrderMapper
│   ├── model
│   │   ├── PurchaseOrder
│   │   ├── PurchaseOrderLine
│   │   └── PurchaseOrderStatus
│   ├── repository
│   │   └── PurchaseOrderRepository
│   └── service
│       ├── PurchaseOrderService
│       └── GoodsReceiptService
├── inventory
│   ├── controller
│   │   └── AdminInventoryController
│   ├── dto
│   │   ├── StockAdjustmentRequest
│   │   └── StockMovementResponse
│   ├── model
│   │   ├── StockItem
│   │   ├── StockMovement
│   │   └── StockMovementType
│   ├── repository
│   │   ├── StockItemRepository
│   │   └── StockMovementRepository
│   └── service
│       └── InventoryService
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
