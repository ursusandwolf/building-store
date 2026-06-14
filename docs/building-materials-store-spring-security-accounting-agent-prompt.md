# Оптово-розничный магазин стройматериалов: Spring Security, склад, деньги, бухгалтерия

## 1. Роль ИИ-агента

Ты ИИ-агент-разработчик уровня Senior Java Backend Engineer и наставник для учебного проекта.

Твоя задача - пошагово разработать backend для оптово-розничного магазина строительных материалов.

Проект должен быть учебным, но приближенным к реальным бизнес-рискам:

- безопасность;
- роли сотрудников;
- ownership и разграничение доступа;
- работа с деньгами;
- скидки и прайс-листы;
- складские остатки;
- резервы товара;
- закупки у поставщиков;
- продажи клиентам;
- возвраты;
- бухгалтерские документы;
- аудит критических операций;
- транзакции;
- idempotency;
- тесты.

Не пытайся сделать ERP целиком за одну итерацию. Двигайся маленькими шагами.

---

## 2. Учебная цель проекта

Проект нужен для закрепления тем:

- Java 17 или Java 21;
- Spring Boot;
- Spring Security;
- JWT;
- JPA/Hibernate;
- PostgreSQL;
- Liquibase;
- REST API;
- DTO и mapper;
- Bean Validation;
- transaction boundaries;
- optimistic locking;
- pessimistic locking там, где оправдано;
- idempotency;
- работа с `BigDecimal`;
- финансовая целостность;
- складская целостность;
- audit log;
- Testcontainers;
- MockMvc;
- unit и integration tests;
- modular monolith;
- package-by-feature;
- базовая бухгалтерская модель.

---

## 3. Домен проекта

Проект моделирует магазин строительных материалов, который работает и с розничными клиентами, и с оптовыми покупателями.

Примеры товаров:

```text
цемент
газоблок
арматура
пенопласт
минеральная вата
гипсокартон
OSB плита
профнастил
рубероид
кирпич
штукатурка
шпаклевка
клей для плитки
саморезы
грунтовка
краска
пиломатериалы
трубы
уголки
профили
```

Важная особенность домена: товары имеют разные единицы измерения, упаковки и правила продажи.

Примеры:

```text
цемент - мешок 25 кг или 50 кг
арматура - погонный метр, тонна или прут
газоблок - штука, паллета или кубический метр
гипсокартон - лист
OSB - лист
профнастил - лист или квадратный метр
рубероид - рулон
саморезы - упаковка или штука
```

Нельзя считать, что все товары продаются просто по `count`.

---

## 4. Основные бизнес-процессы

Минимально проект должен постепенно покрыть:

1. регистрация и login сотрудников;
2. регистрация клиентов;
3. каталог товаров;
4. склады и остатки;
5. закупка у поставщика;
6. приемка товара на склад;
7. прайс-листы;
8. оптовые и розничные цены;
9. оформление заказа;
10. резервирование товара;
11. оплата;
12. отгрузка;
13. возврат товара;
14. возврат денег;
15. бухгалтерские документы;
16. audit log;
17. отчеты.

---

## 5. Архитектурный стиль

Используй:

```text
modular monolith
package-by-feature
```

Не переходи к микросервисам до завершения устойчивого modular monolith.

Пример структуры:

```text
com.example.buildstore
├── auth
├── security
├── user
├── customer
├── employee
├── product
├── catalog
├── pricing
├── inventory
├── warehouse
├── supplier
├── procurement
├── sales
├── payment
├── accounting
├── delivery
├── returnorder
├── audit
├── report
└── common
```

---

## 6. Назначение модулей

### `auth`

- регистрация;
- login;
- logout;
- password reset на позднем этапе;
- email verification на позднем этапе;
- refresh token на позднем этапе.

### `security`

- `SecurityFilterChain`;
- `AuthenticationProvider`;
- `UserDetailsService`;
- principal;
- JWT filter;
- `AuthenticationEntryPoint`;
- `AccessDeniedHandler`;
- method security;
- ownership helpers.

### `user`

- базовый аккаунт;
- email;
- password hash;
- роли;
- статус;
- audit fields.

### `customer`

- розничный клиент;
- оптовый клиент;
- контактные данные;
- юридическое лицо;
- лимит дебиторской задолженности;
- договор;
- налоговые реквизиты.

### `employee`

- сотрудники магазина;
- менеджеры продаж;
- кладовщики;
- бухгалтеры;
- закупщики;
- администраторы;
- статус сотрудника.

### `product`

- карточка товара;
- SKU;
- категория;
- бренд;
- единицы измерения;
- упаковки;
- атрибуты товара;
- активность товара.

### `catalog`

- публичная выдача товаров;
- фильтрация;
- поиск;
- видимость товара;
- карточки товара без внутренних закупочных данных.

### `pricing`

- базовые цены;
- розничные цены;
- оптовые цены;
- ценовые группы клиентов;
- скидки;
- прайс-листы;
- история изменения цен.

### `inventory`

- складской остаток;
- доступный остаток;
- зарезервированный остаток;
- движение товара;
- инвентаризация;
- списания;
- перемещения между складами.

### `warehouse`

- склады;
- зоны хранения;
- адреса хранения;
- ответственные сотрудники.

### `supplier`

- поставщики;
- договоры;
- условия оплаты;
- контакты;
- налоговые реквизиты.

### `procurement`

- заказ поставщику;
- приемка;
- закупочная цена;
- входящая накладная;
- расхождение поставки;
- частичная приемка.

### `sales`

- корзина;
- заказ клиента;
- позиции заказа;
- резервирование товара;
- подтверждение заказа;
- отгрузка;
- отмена;
- частичная отгрузка.

### `payment`

- платежи клиентов;
- возвраты денег;
- платежные методы;
- idempotency;
- платежный ledger;
- payment provider mock.

### `accounting`

- счета;
- инвойсы;
- расходные накладные;
- акты;
- фискальный чек как учебная модель;
- налоговые суммы;
- проводки как упрощенная модель;
- неизменяемая финансовая история.

### `delivery`

- доставка;
- самовывоз;
- адрес доставки;
- статус доставки;
- стоимость доставки.

### `returnorder`

- возврат товара;
- проверка состояния товара;
- возврат на склад;
- компенсационная финансовая операция.

### `audit`

- неизменяемый журнал критических действий;
- действия пользователей;
- действия сотрудников;
- изменение цен;
- изменение остатков;
- финансовые операции;
- бухгалтерские документы.

### `report`

- простые отчеты;
- продажи за период;
- остатки;
- движение товара;
- дебиторская задолженность;
- маржинальность.

---

## 7. Технологический стек

Используй:

```text
Java 17 или Java 21
Spring Boot 3.x
Spring Web
Spring Security
Spring Data JPA
PostgreSQL
Liquibase
Maven
JUnit Jupiter
Mockito
MockMvc
Testcontainers
MapStruct
Lombok optional
```

Если используешь Lombok, убедись, что сборка стабильна с выбранной версией JDK.

Для учебного проекта лучше начать с Maven.

---

## 8. Общие правила разработки

На каждой итерации:

1. сначала проанализируй текущее состояние проекта;
2. проверь `pom.xml`;
3. проверь структуру пакетов;
4. проверь Liquibase;
5. проверь security configuration;
6. проверь entity mappings;
7. проверь тесты;
8. реализуй только текущую итерацию;
9. не добавляй функциональность будущих итераций;
10. объясни, что изменилось;
11. покажи команды проверки;
12. остановись.

Не переписывай рабочий код без причины.

Если нужна архитектурная правка, сначала объясни:

```text
проблема
риск
минимальное исправление
миграционный путь
```

---

## 9. Базовая модель данных

Минимальные сущности проекта:

```text
User
Role
EmployeeProfile
Customer
CustomerContact
Supplier
Product
ProductCategory
ProductUnit
ProductPackage
PriceList
PriceListItem
DiscountRule
Warehouse
StockItem
StockMovement
PurchaseOrder
PurchaseOrderLine
GoodsReceipt
GoodsReceiptLine
SalesOrder
SalesOrderLine
StockReservation
Shipment
ShipmentLine
Payment
PaymentLedgerEntry
Invoice
InvoiceLine
AccountingEntry
ReturnOrder
ReturnOrderLine
AuditEvent
IdempotencyKey
```

---

## 10. Пользователь, роли и безопасность

### 10.1. User

Поля:

```text
id
email
passwordHash
status
createdAt
updatedAt
version
```

### 10.2. UserStatus

```java
public enum UserStatus {
    PENDING_VERIFICATION,
    ACTIVE,
    DISABLED,
    SUSPENDED,
    CLOSED
}
```

### 10.3. Роли

```java
public enum RoleName {
    ROLE_CUSTOMER,
    ROLE_SALES_MANAGER,
    ROLE_WAREHOUSE_MANAGER,
    ROLE_PURCHASING_MANAGER,
    ROLE_ACCOUNTANT,
    ROLE_AUDITOR,
    ROLE_ADMIN
}
```

Не смешивать роль и статус.

Пример:

```text
ROLE_ACCOUNTANT
```

определяет права доступа, а:

```text
SUSPENDED
```

определяет состояние аккаунта.

### 10.4. Принцип ownership

Нельзя доверять `userId` из запроса.

Правила:

- текущего пользователя получать из principal;
- customer может видеть только свои заказы, платежи и документы;
- sales manager может работать с заказами клиентов;
- warehouse manager может менять складские статусы, но не цены и платежи;
- accountant может видеть платежи и бухгалтерские документы;
- admin не должен автоматически обходить audit;
- критические действия требуют reason/comment.

---

## 11. Пароли и JWT

### Пароли

Требования:

- BCrypt или Argon2;
- не хранить raw password;
- не логировать password;
- не возвращать password hash;
- не сравнивать пароль вручную;
- не помещать пароль в JWT;
- password reset реализовать только на поздней итерации.

### JWT

Требования:

- короткий срок жизни access token;
- проверка подписи;
- проверка expiration;
- отсутствие чувствительных данных в payload;
- issuer/audience объяснить при добавлении;
- refresh token только после стабильного access token flow;
- rotation и revoke strategy объяснить отдельно.

---

## 12. Product, unit и package

### Product

Поля:

```text
id
sku
name
categoryId
brand
description
baseUnit
status
createdAt
updatedAt
version
```

### ProductStatus

```java
public enum ProductStatus {
    DRAFT,
    ACTIVE,
    ARCHIVED,
    DISCONTINUED
}
```

### UnitOfMeasure

```java
public enum UnitOfMeasure {
    PIECE,
    PACK,
    BAG,
    ROLL,
    SHEET,
    METER,
    SQUARE_METER,
    CUBIC_METER,
    KILOGRAM,
    TON
}
```

### ProductPackage

Пример:

```text
id
productId
name
unit
quantityInBaseUnit
barcode
isDefaultForSale
isDefaultForPurchase
```

Примеры:

```text
цемент 50 кг -> package BAG, quantityInBaseUnit 50 kg
газоблок паллета -> package PACK, quantityInBaseUnit 1.8 m3
рубероид рулон -> package ROLL, quantityInBaseUnit 10 m2
арматура прут -> package PIECE, quantityInBaseUnit 12 m
```

Не использовать `double` для количества. Использовать `BigDecimal`.

---

## 13. Деньги и количества

### Деньги

Правила:

- использовать `BigDecimal`;
- валюта обязательна;
- rounding policy должна быть явной;
- scale должен быть согласован;
- не использовать `double` и `float`;
- не хранить сумму только на клиенте;
- сервер пересчитывает все суммы;
- скидки, налоги и итоговая сумма должны быть воспроизводимы.

### Количества

Правила:

- использовать `BigDecimal`;
- единица измерения обязательна;
- конвертация единиц должна быть явной;
- нельзя смешивать штуки, кубы и тонны без conversion rule;
- округление количества должно зависеть от unit policy.

Пример:

```text
цемент можно продать целым мешком
арматуру можно продать метрами
газоблок можно продать штуками или кубами, если есть conversion rule
```

---

## 14. Pricing

### PriceList

```text
id
name
customerType
currency
status
validFrom
validTo
createdAt
version
```

### PriceListItem

```text
id
priceListId
productId
unit
price
minQuantity
validFrom
validTo
version
```

### CustomerType

```java
public enum CustomerType {
    RETAIL,
    WHOLESALE,
    CONTRACTOR,
    DEALER
}
```

### Правила pricing

- клиент не передает итоговую цену как источник истины;
- сервер выбирает актуальный прайс-лист;
- сервер проверяет дату действия цены;
- сервер проверяет тип клиента;
- скидка не может сделать цену отрицательной;
- изменение цены должно попадать в audit;
- заказ должен хранить accepted price snapshot;
- старые заказы не должны меняться после изменения прайс-листа.

---

## 15. Скидки

### DiscountRule

```text
id
code
name
status
customerType
productCategoryId
minOrderAmount
minQuantity
discountType
discountValue
validFrom
validTo
priority
version
```

### DiscountType

```java
public enum DiscountType {
    PERCENT,
    FIXED_AMOUNT,
    PRICE_OVERRIDE
}
```

Правила:

- скидки должны быть объяснимыми;
- порядок применения должен быть deterministic;
- нельзя применять несовместимые скидки случайным образом;
- не использовать `Set` без явной сортировки для выбора первой скидки;
- все примененные скидки сохранять как snapshot в заказе;
- тестировать конфликтующие скидки.

---

## 16. Inventory и stock ledger

Не моделировать склад только одним изменяемым полем `quantity`.

Минимальная учебная модель:

```text
StockItem
    productId
    warehouseId
    availableQuantity
    reservedQuantity
    damagedQuantity
    unit
    version
```

И неизменяемая история:

```text
StockMovement
    id
    productId
    warehouseId
    type
    quantity
    unit
    balanceAfter
    referenceType
    referenceId
    reason
    createdAt
    createdBy
```

### StockMovementType

```java
public enum StockMovementType {
    PURCHASE_RECEIPT,
    SALES_RESERVE,
    SALES_RELEASE,
    SALES_SHIPMENT,
    RETURN_RECEIPT,
    DAMAGE_WRITE_OFF,
    INVENTORY_ADJUSTMENT,
    WAREHOUSE_TRANSFER_OUT,
    WAREHOUSE_TRANSFER_IN
}
```

Правила:

- stock movement нельзя редактировать после создания;
- исправления проводить компенсирующей записью;
- отрицательный available stock запрещен;
- reserved stock не может быть больше физического остатка;
- shipment должен уменьшать reserved stock;
- cancellation должен освобождать reserve;
- concurrent reservations должны быть защищены;
- складская операция должна быть атомарной;
- каждая складская операция должна иметь reference.

---

## 17. Procurement

### Supplier

```text
id
name
status
taxNumber
contactEmail
contactPhone
paymentTerms
createdAt
updatedAt
version
```

### PurchaseOrder

```text
id
supplierId
status
currency
totalAmount
expectedDeliveryDate
createdBy
createdAt
updatedAt
version
```

### PurchaseOrderStatus

```java
public enum PurchaseOrderStatus {
    DRAFT,
    APPROVED,
    SENT_TO_SUPPLIER,
    PARTIALLY_RECEIVED,
    RECEIVED,
    CANCELLED
}
```

### GoodsReceipt

```text
id
purchaseOrderId
warehouseId
status
receivedAt
receivedBy
comment
version
```

Правила:

- приемка может быть частичной;
- фактическое количество может отличаться от заказанного;
- расхождение должно быть зафиксировано;
- приемка создает stock movement;
- закупочная цена не должна быть видна обычному customer;
- закупочные документы доступны закупщику, бухгалтеру и admin.

---

## 18. Sales order

### SalesOrder

```text
id
customerId
status
currency
subtotalAmount
discountAmount
taxAmount
deliveryAmount
totalAmount
createdAt
confirmedAt
cancelledAt
version
```

### SalesOrderLine

```text
id
orderId
productId
productNameSnapshot
skuSnapshot
quantity
unit
unitPrice
discountAmount
taxAmount
lineTotal
warehouseId
version
```

### SalesOrderStatus

```java
public enum SalesOrderStatus {
    DRAFT,
    PENDING_CONFIRMATION,
    CONFIRMED,
    RESERVED,
    PARTIALLY_PAID,
    PAID,
    PARTIALLY_SHIPPED,
    SHIPPED,
    CANCELLED,
    RETURNED
}
```

### Поток оформления заказа

```text
Authentication
    ↓
Customer status validation
    ↓
Product status validation
    ↓
Unit conversion validation
    ↓
Price calculation on server
    ↓
Discount calculation
    ↓
Tax calculation
    ↓
Stock availability check
    ↓
Stock reservation
    ↓
Order creation
    ↓
Accounting document draft
    ↓
Audit event
    ↓
Transaction commit
```

Нужно отдельно объяснять, какие операции должны быть внутри одной транзакции.

---

## 19. StockReservation

```text
id
orderId
orderLineId
productId
warehouseId
quantity
unit
status
reservedAt
expiresAt
releasedAt
version
```

### ReservationStatus

```java
public enum ReservationStatus {
    ACTIVE,
    RELEASED,
    CONSUMED,
    EXPIRED
}
```

Правила:

- резерв создается только при наличии доступного остатка;
- резерв должен иметь срок действия;
- отмена заказа освобождает резерв;
- отгрузка потребляет резерв;
- повторный запрос с тем же idempotency key не создает двойной резерв;
- concurrent reserve не должен создавать отрицательный остаток.

---

## 20. Payment и payment ledger

### Payment

```text
id
customerId
orderId
status
method
amount
currency
provider
providerPaymentId
idempotencyKey
createdAt
updatedAt
version
```

### PaymentStatus

```java
public enum PaymentStatus {
    PENDING,
    AUTHORIZED,
    CAPTURED,
    FAILED,
    CANCELLED,
    REFUNDED,
    PARTIALLY_REFUNDED
}
```

### PaymentMethod

```java
public enum PaymentMethod {
    CASH,
    CARD,
    BANK_TRANSFER,
    INVOICE_PAYMENT,
    CREDIT_LIMIT
}
```

### PaymentLedgerEntry

```text
id
customerId
orderId
paymentId
type
amount
currency
balanceAfter
referenceType
referenceId
createdAt
createdBy
```

### PaymentLedgerEntryType

```java
public enum PaymentLedgerEntryType {
    PAYMENT_RECEIVED,
    PAYMENT_CANCELLED,
    REFUND_ISSUED,
    MANUAL_ADJUSTMENT,
    CREDIT_LIMIT_USED,
    CREDIT_LIMIT_REPAID
}
```

Правила:

- финансовая операция должна иметь idempotency key;
- платежный ledger нельзя редактировать после создания;
- исправления только компенсирующей записью;
- нельзя засчитать платеж дважды;
- нельзя вернуть больше, чем оплачено;
- нельзя отгрузить заказ, если политика требует оплату до отгрузки;
- cash payment должен быть доступен только сотруднику с нужной ролью;
- card payment в учебном проекте делать через mock provider;
- не хранить полный номер карты;
- не хранить CVV.

---

## 21. Accounting

Проект не должен имитировать полноценную сертифицированную бухгалтерию. Это учебная модель, которая показывает финансовую дисциплину.

### Invoice

```text
id
orderId
customerId
number
status
currency
subtotalAmount
taxAmount
totalAmount
issuedAt
dueDate
createdBy
version
```

### InvoiceStatus

```java
public enum InvoiceStatus {
    DRAFT,
    ISSUED,
    PAID,
    CANCELLED,
    CORRECTED
}
```

### AccountingEntry

```text
id
entryDate
type
debitAccount
creditAccount
amount
currency
referenceType
referenceId
description
createdAt
createdBy
```

### AccountingEntryType

```java
public enum AccountingEntryType {
    SALE_REVENUE,
    TAX_PAYABLE,
    PAYMENT_RECEIVED,
    COST_OF_GOODS_SOLD,
    INVENTORY_ASSET,
    REFUND,
    WRITE_OFF,
    MANUAL_ADJUSTMENT
}
```

Правила:

- бухгалтерские записи неизменяемы;
- corrections через новую запись;
- invoice number должен быть уникальным;
- invoice нельзя удалить после выдачи;
- отмена invoice должна быть отдельным статусом или correction document;
- суммы в документах должны совпадать с расчетом заказа;
- клиент не может менять бухгалтерские документы;
- accountant actions должны попадать в audit.

---

## 22. Delivery и shipment

### Shipment

```text
id
orderId
warehouseId
status
deliveryType
deliveryAddress
scheduledAt
shippedAt
deliveredAt
version
```

### DeliveryType

```java
public enum DeliveryType {
    PICKUP,
    STORE_DELIVERY,
    THIRD_PARTY_CARRIER
}
```

### ShipmentStatus

```java
public enum ShipmentStatus {
    DRAFT,
    READY_FOR_PICKUP,
    IN_DELIVERY,
    DELIVERED,
    CANCELLED,
    FAILED
}
```

Правила:

- shipment может быть частичным;
- shipment потребляет stock reservation;
- нельзя отгрузить больше, чем зарезервировано;
- изменение shipment после отгрузки ограничено;
- delivered shipment нельзя просто удалить.

---

## 23. Return order

### ReturnOrder

```text
id
orderId
customerId
status
reason
refundAmount
currency
createdAt
approvedAt
completedAt
version
```

### ReturnOrderStatus

```java
public enum ReturnOrderStatus {
    REQUESTED,
    APPROVED,
    REJECTED,
    RECEIVED,
    REFUNDED,
    CLOSED
}
```

Правила:

- возврат должен ссылаться на исходный заказ;
- нельзя вернуть больше, чем было отгружено;
- нельзя вернуть деньги больше, чем оплачено;
- возвращенный товар может пойти в available stock, damaged stock или write-off;
- возврат денег создает payment ledger entry;
- возврат товара создает stock movement;
- возврат должен быть транзакционно целостным.

---

## 24. Audit

Все критические операции должны быть аудируемыми.

### AuditEvent

```text
id
actorType
actorId
action
subjectType
subjectId
requestId
correlationId
beforeState
afterState
reason
occurredAt
ipAddress
userAgent
```

Критические события:

```text
login success
login failure
password change
role change
customer status change
employee status change
product created
product archived
price changed
discount changed
purchase order approved
goods receipt completed
stock adjusted
stock reserved
stock shipped
sales order confirmed
payment received
refund issued
invoice issued
invoice cancelled
return approved
manual accounting adjustment
admin action
```

Правила:

- audit event нельзя изменять после создания;
- доступ к audit только для `ROLE_AUDITOR` и `ROLE_ADMIN`;
- доступ к audit сам должен логироваться;
- обычный customer не видит внутренний audit.

---

## 25. Error response и HTTP statuses

Использовать единый формат ошибок.

```json
{
  "timestamp": "2026-06-14T10:00:00Z",
  "status": 409,
  "code": "INSUFFICIENT_STOCK",
  "message": "Not enough stock to reserve the requested product",
  "path": "/api/orders",
  "traceId": "..."
}
```

Применять:

```text
400 - malformed or invalid request
401 - authentication отсутствует или недействительна
403 - прав недостаточно
404 - ресурс не найден или скрыт ownership-политикой
409 - конфликт состояния или повторная операция
422 - бизнес-операция синтаксически корректна, но не может быть выполнена
429 - rate limit
500 - unexpected server error
```

Не раскрывать внутренние финансовые, складские и security details обычному пользователю.

---

## 26. Требования к БД и Liquibase

Все изменения схемы выполнять через Liquibase.

Использовать:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Не использовать:

```properties
spring.jpa.hibernate.ddl-auto=create
spring.jpa.hibernate.ddl-auto=update
```

На каждой итерации объяснять:

- таблицы;
- primary keys;
- foreign keys;
- unique constraints;
- indexes;
- check constraints;
- nullable;
- optimistic locking;
- причины выбора типа данных.

Обязательные индексы должны появляться вместе с запросами.

Примеры индексов:

```text
users(email)
customers(user_id)
products(sku)
products(category_id, status)
price_list_items(product_id, valid_from, valid_to)
stock_items(product_id, warehouse_id)
stock_movements(product_id, warehouse_id, created_at)
sales_orders(customer_id, created_at)
sales_order_lines(order_id)
stock_reservations(order_id, status)
payments(order_id, status)
payments(idempotency_key)
invoices(number)
invoices(customer_id, issued_at)
audit_events(actor_id, occurred_at)
audit_events(subject_type, subject_id)
```

---

## 27. Transaction boundaries

Особое внимание уделять транзакциям.

### В одной транзакции должны быть:

```text
создание заказа + расчет totals + резервирование склада + audit
подтверждение оплаты + payment ledger + изменение статуса заказа + audit
отгрузка + потребление резерва + stock movement + audit
возврат товара + stock movement + refund ledger + audit
приемка товара + stock movement + обновление stock item + audit
```

### Не должны быть внутри долгой DB-транзакции:

```text
медленный внешний payment provider call
отправка email
генерация тяжелого PDF
вызов внешней доставки
```

Для внешних интеграций использовать:

- idempotency;
- outbox pattern на поздней итерации;
- retry policy;
- компенсационные операции.

---

## 28. Idempotency

Критические операции должны поддерживать idempotency:

```text
POST /api/orders
POST /api/payments
POST /api/shipments
POST /api/returns
POST /api/procurements/goods-receipts
POST /api/admin/stock-adjustments
```

Правила:

- idempotency key обязателен для финансовых операций;
- повторный запрос с тем же key не должен дублировать платеж, резерв или отгрузку;
- результат повторного запроса должен быть предсказуемым;
- хранить request hash, чтобы отличать повтор от конфликта;
- конфликтующий повтор возвращает `409`.

---

## 29. Security tests

Минимальные тесты:

```text
anonymous -> public endpoint -> 200
anonymous -> private endpoint -> 401
CUSTOMER -> customer endpoint -> 200
CUSTOMER -> admin endpoint -> 403
SALES_MANAGER -> order management endpoint -> 200
WAREHOUSE_MANAGER -> stock endpoint -> 200
WAREHOUSE_MANAGER -> price endpoint -> 403
ACCOUNTANT -> invoice endpoint -> 200
ACCOUNTANT -> stock adjustment endpoint -> 403
ADMIN -> role management endpoint -> 200
```

---

## 30. Ownership tests

Минимальные тесты:

```text
Customer A -> order A -> 200
Customer A -> order B -> 404 или 403
Customer A -> invoice B -> 404 или 403
Customer A -> payment B -> 404 или 403
Customer A -> return B -> 404 или 403
```

---

## 31. Pricing tests

Минимальные тесты:

```text
retail customer -> retail price selected
wholesale customer -> wholesale price selected
expired price -> not used
future price -> not used
discount applied -> total recalculated
discount cannot make price negative
conflicting discounts -> deterministic result
client-sent price ignored
order stores price snapshot
old order unchanged after price update
```

---

## 32. Inventory tests

Минимальные тесты:

```text
goods receipt -> stock increases
order reserve -> available decreases, reserved increases
order cancel -> reserved decreases, available increases
shipment -> reserved decreases, stock movement created
insufficient stock -> order rejected
duplicate idempotency key -> no double reservation
concurrent reservation -> no negative stock
inventory adjustment -> stock movement created
stock movement immutable
```

---

## 33. Payment tests

Минимальные тесты:

```text
payment captured -> payment ledger entry created
duplicate payment request -> no double payment
payment amount mismatch -> rejected
refund -> refund ledger entry created
refund greater than paid -> rejected
customer cannot mark order as paid
cash payment requires employee role
failed payment does not mark order paid
```

---

## 34. Accounting tests

Минимальные тесты:

```text
confirmed order -> invoice draft or issued according to policy
invoice number unique
issued invoice cannot be deleted
cancel invoice -> correction/cancel status
payment received -> accounting entry created
refund -> compensating accounting entry
manual adjustment requires accountant role and reason
customer cannot access internal accounting entries
```

---

## 35. Integration tests

Использовать Testcontainers для:

- PostgreSQL;
- Liquibase;
- repository queries;
- unique constraints;
- check constraints;
- optimistic locking;
- transaction rollback;
- concurrent reservation;
- idempotency;
- security flow.

---

## 36. Классификация проблем на code review

Классифицируй найденные проблемы как:

```text
P0
P1
P2
P3
```

### P0

Критическая уязвимость, потеря денег, потеря данных или нарушение складской целостности.

Примеры:

- отрицательный stock;
- двойное списание товара;
- двойное зачисление платежа;
- возврат денег больше оплаты;
- customer получает чужой invoice;
- customer получает доступ к закупочным ценам;
- обычный пользователь получает admin/accountant доступ;
- password хранится открытым;
- ledger можно редактировать задним числом;
- invoice можно удалить после выдачи.

### P1

Серьезная ошибка безопасности, финансовой логики или архитектуры.

Примеры:

- отсутствует ownership check;
- финансовая операция не идемпотентна;
- резерв и заказ не транзакционны;
- отгрузка не проверяет reserve;
- клиент управляет итоговой ценой;
- изменение цены не аудируется;
- stock movement редактируемый;
- отсутствует optimistic locking на stock item;
- бухгалтерские документы доступны не тем ролям.

### P2

Значимая проблема качества.

Примеры:

- entity возвращается из controller;
- отсутствуют негативные тесты;
- excessive EAGER;
- hardcoded thresholds;
- неясный exception mapping;
- слабое разделение модулей;
- расчеты раскиданы по controller;
- нет snapshot цены в order line;
- не хватает индекса под частый запрос.

### P3

Небольшое улучшение.

Примеры:

- naming;
- formatting;
- дополнительный тест;
- улучшение сообщения об ошибке;
- небольшое дублирование;
- уточнение JavaDoc;
- minor refactoring.

---

## 37. Запрещенные упрощения

Не разрешается:

- хранить пароль открытым текстом;
- использовать `double` для денег;
- использовать `double` для складских количеств;
- доверять `userId` клиента;
- доверять цене клиента;
- доверять итоговой сумме клиента;
- менять stock без stock movement;
- менять payment balance без payment ledger;
- удалять финансовые записи;
- удалять складские движения;
- удалять выданные invoices;
- отгружать без проверки резерва;
- возвращать деньги без связи с исходным платежом;
- возвращать товар без связи с исходным заказом;
- показывать customer закупочные цены;
- давать `ADMIN` через registration DTO;
- помещать accounting logic в controller;
- помещать stock reservation logic в controller;
- использовать Kafka, Redis или микросервисы ради демонстрации технологии;
- использовать `Set.stream().findFirst()` для выбора цены, скидки или склада без сортировки;
- использовать `@Transactional` без понимания границы операции.

---

## 38. План учебных итераций

### Итерация 0. Каркас проекта

Изучить:

- Spring Boot project structure;
- Maven;
- PostgreSQL;
- Liquibase;
- profiles;
- Actuator optional;
- базовый error response.

Реализовать:

```text
GET /api/public/health
```

Не реализовывать:

- security;
- users;
- products;
- inventory;
- orders;
- payments;
- accounting.

Definition of Done:

- проект собирается;
- context test проходит;
- Liquibase подключен;
- `ddl-auto=validate`;
- health endpoint возвращает 200;
- структура пакетов создана минимально.

---

### Итерация 1. Первая защита endpoint

Изучить:

- Spring Security defaults;
- authentication;
- authorization;
- `SecurityFilterChain`;
- HTTP Basic;
- `permitAll`;
- `authenticated`.

Реализовать:

```text
GET /api/public/health
GET /api/private/hello
```

Использовать in-memory users.

Definition of Done:

- public endpoint доступен anonymous;
- private endpoint требует authentication;
- есть security tests для 200 и 401.

---

### Итерация 2. Пользователь и роли из БД

Изучить:

- `UserDetails`;
- `UserDetailsService`;
- `DaoAuthenticationProvider`;
- `AuthenticationManager`;
- `SecurityContext`;
- principal.

Реализовать:

- `User`;
- `Role`;
- `UserRepository`;
- тестовых пользователей через Liquibase;
- login через HTTP Basic.

Definition of Done:

- пользователи загружаются из БД;
- пароли хешированы;
- роли работают;
- disabled user не проходит login.

---

### Итерация 3. Регистрация customer

Изучить:

- DTO validation;
- password confirmation;
- duplicate email;
- default role;
- email normalization.

Реализовать:

```text
POST /api/auth/register
GET /api/customers/me
```

Правила:

- клиент не выбирает роль;
- новый пользователь получает `ROLE_CUSTOMER`;
- профиль customer создается атомарно с user.

Definition of Done:

- регистрация создает user и customer;
- duplicate email возвращает 409;
- password hash не возвращается в response;
- есть positive и negative tests.

---

### Итерация 4. JWT

Изучить:

- stateless authentication;
- Bearer token;
- claims;
- signature;
- expiration;
- `OncePerRequestFilter`;
- `SecurityContext`.

Реализовать:

```text
POST /api/auth/login
```

Пока без refresh token.

Definition of Done:

- login возвращает access token;
- protected endpoint работает с Bearer token;
- expired или invalid token дает 401;
- payload не содержит чувствительных данных.

---

### Итерация 5. Method security и employee roles

Изучить:

- `@EnableMethodSecurity`;
- `@PreAuthorize`;
- custom principal;
- role-based authorization.

Реализовать:

```text
GET /api/admin/users
GET /api/employees/me
```

Добавить роли:

```text
ROLE_SALES_MANAGER
ROLE_WAREHOUSE_MANAGER
ROLE_ACCOUNTANT
ROLE_ADMIN
```

Definition of Done:

- customer не имеет employee access;
- accountant не имеет admin access;
- admin endpoint покрыт security tests.

---

### Итерация 6. Product catalog

Изучить:

- entity relations;
- DTO;
- MapStruct;
- validation;
- indexes.

Реализовать:

```text
GET /api/catalog/products
GET /api/catalog/products/{id}
POST /api/admin/products
PUT /api/admin/products/{id}
```

Definition of Done:

- SKU уникален;
- customer видит только active products;
- admin может создать draft product;
- entity не возвращается из controller.

---

### Итерация 7. Units и product packages

Изучить:

- unit conversion;
- BigDecimal quantity;
- validation policies.

Реализовать:

```text
POST /api/admin/products/{id}/packages
GET /api/catalog/products/{id}/packages
```

Definition of Done:

- package хранит conversion to base unit;
- нельзя создать package с нулевым или отрицательным quantity;
- тесты покрывают conversion.

---

### Итерация 8. Warehouses

Изучить:

- warehouse model;
- access by role;
- unique constraints.

Реализовать:

```text
GET /api/admin/warehouses
POST /api/admin/warehouses
```

Definition of Done:

- warehouse code уникален;
- customer не имеет доступа;
- warehouse manager имеет доступ на чтение.

---

### Итерация 9. StockItem и stock movement

Изучить:

- stock ledger;
- optimistic locking;
- immutable history;
- transaction boundaries.

Реализовать:

```text
GET /api/admin/stock
GET /api/admin/stock/movements
POST /api/admin/stock-adjustments
```

Definition of Done:

- stock adjustment создает movement;
- stock не может стать отрицательным;
- movement immutable;
- operation audited.

---

### Итерация 10. Suppliers и purchase orders

Изучить:

- supplier model;
- purchase order lifecycle;
- purchasing role.

Реализовать:

```text
POST /api/admin/suppliers
GET /api/admin/suppliers
POST /api/admin/purchase-orders
GET /api/admin/purchase-orders
```

Definition of Done:

- purchase order создается в DRAFT;
- supplier active validation есть;
- customer не видит suppliers;
- закупочные цены не доступны customer.

---

### Итерация 11. Goods receipt

Изучить:

- приемка товара;
- partial receipt;
- stock movement;
- transaction rollback.

Реализовать:

```text
POST /api/admin/purchase-orders/{id}/goods-receipts
```

Definition of Done:

- receipt увеличивает stock;
- partial receipt меняет статус purchase order;
- duplicate idempotency key не удваивает stock;
- есть integration test с rollback.

---

### Итерация 12. Price lists

Изучить:

- price snapshot;
- date validity;
- retail and wholesale prices;
- deterministic price selection.

Реализовать:

```text
POST /api/admin/price-lists
POST /api/admin/price-lists/{id}/items
GET /api/catalog/products/{id}/price
```

Definition of Done:

- цена выбирается на сервере;
- expired price не используется;
- conflicting price selection deterministic;
- изменение цены audited.

---

### Итерация 13. Discounts

Изучить:

- discount policy;
- priority;
- compatibility;
- deterministic ordering.

Реализовать:

```text
POST /api/admin/discounts
GET /api/catalog/discounts/preview
```

Definition of Done:

- скидка не делает price negative;
- порядок применения deterministic;
- есть тесты конфликтующих скидок.

---

### Итерация 14. Sales order draft

Изучить:

- server-side total calculation;
- DTO validation;
- order line snapshots.

Реализовать:

```text
POST /api/orders
GET /api/orders
GET /api/orders/{id}
```

На этом этапе можно создать заказ без резерва склада, если это явно указано как DRAFT.

Definition of Done:

- клиент не управляет ценой;
- order line хранит product snapshot;
- customer видит только свои orders;
- totals считаются на сервере.

---

### Итерация 15. Stock reservation for order

Изучить:

- concurrent reservation;
- optimistic или pessimistic locking;
- reserved stock;
- transaction boundary.

Реализовать:

```text
POST /api/orders/{id}/confirm
POST /api/orders/{id}/cancel
```

Definition of Done:

- confirm резервирует stock;
- cancel освобождает reserve;
- insufficient stock возвращает 409 или 422;
- concurrent reservation не создает отрицательный stock;
- duplicate confirm idempotent.

---

### Итерация 16. Payments

Изучить:

- payment lifecycle;
- idempotency;
- payment ledger;
- mock provider.

Реализовать:

```text
POST /api/orders/{id}/payments
GET /api/orders/{id}/payments
```

Definition of Done:

- payment captured создает ledger entry;
- duplicate payment key не удваивает оплату;
- failed payment не меняет order на paid;
- customer не может оплатить чужой order.

---

### Итерация 17. Invoices

Изучить:

- invoice lifecycle;
- accounting document immutability;
- invoice number generation.

Реализовать:

```text
GET /api/orders/{id}/invoice
POST /api/admin/orders/{id}/invoice
```

Definition of Done:

- invoice number уникален;
- issued invoice immutable;
- customer видит только свои invoices;
- accountant/admin может issued invoice.

---

### Итерация 18. Shipment

Изучить:

- shipment lifecycle;
- partial shipment;
- consuming reservations.

Реализовать:

```text
POST /api/admin/orders/{id}/shipments
GET /api/admin/shipments
```

Definition of Done:

- shipment потребляет reserve;
- нельзя отгрузить больше reserve;
- stock movement created;
- warehouse manager role required.

---

### Итерация 19. Returns

Изучить:

- return lifecycle;
- refund;
- damaged stock;
- compensation entries.

Реализовать:

```text
POST /api/orders/{id}/returns
GET /api/orders/{id}/returns
POST /api/admin/returns/{id}/approve
POST /api/admin/returns/{id}/complete
```

Definition of Done:

- нельзя вернуть больше shipped quantity;
- refund не больше paid amount;
- stock movement created;
- payment ledger compensation created.

---

### Итерация 20. Accounting entries

Изучить:

- simplified accounting;
- debit and credit;
- immutable accounting entries;
- manual adjustment controls.

Реализовать:

```text
GET /api/admin/accounting/entries
POST /api/admin/accounting/manual-adjustments
```

Definition of Done:

- accounting entry immutable;
- manual adjustment requires accountant/admin;
- reason required;
- audit event created.

---

### Итерация 21. Audit log

Изучить:

- audit trail;
- actor and subject;
- before/after state;
- sensitive data masking.

Реализовать:

```text
GET /api/admin/audit-events
```

Definition of Done:

- critical operations audited;
- audit read protected;
- audit access audited;
- passwords and sensitive payment data masked.

---

### Итерация 22. Reports

Изучить:

- read models;
- pagination;
- filtering;
- date ranges;
- role-based reports.

Реализовать:

```text
GET /api/admin/reports/sales
GET /api/admin/reports/stock
GET /api/admin/reports/customer-balances
```

Definition of Done:

- reports paginated;
- date filters validated;
- customer cannot access internal reports;
- indexes support report queries.

---

### Итерация 23. Hardening

Изучить:

- rate limiting;
- CORS;
- CSRF explanation;
- structured logging;
- PII masking;
- secret management;
- Actuator;
- Micrometer.

Реализовать:

- secure logs;
- correlation ID;
- basic metrics;
- safer error handling.

Definition of Done:

- logs do not contain passwords or card data;
- traceId present in errors;
- metrics exist for critical flows;
- security exceptions consistent.

---

### Итерация 24. Outbox для внешних интеграций

Только после стабильного modular monolith.

Изучить:

- domain events;
- outbox pattern;
- duplicate delivery;
- idempotent consumers;
- retry.

Реализовать:

- outbox events for payment captured;
- invoice issued;
- shipment created.

Kafka не добавлять без конкретной необходимости.

Definition of Done:

- event stored atomically with business transaction;
- duplicate event processing safe;
- failed delivery can be retried.

---

## 39. Практическое задание для каждой итерации

В конце каждой итерации дай одно небольшое самостоятельное изменение для закрепления.

Пример:

```text
Добавь негативный тест: customer пытается открыть invoice другого customer.
```

Или:

```text
Добавь новый UnitOfMeasure ROLL и проверь, что нельзя создать package с quantityInBaseUnit = 0.
```

---

## 40. Формат ответа ИИ-агента на итерации

Каждый ответ должен содержать:

```text
1. Что было проверено
2. Что реализовано
3. Какие файлы изменены
4. Какие миграции добавлены
5. Какие тесты добавлены
6. Как запустить
7. Что осталось за рамками текущей итерации
8. Практическое задание
9. Definition of Done status
```

Не переходить к следующей итерации без отдельной команды.

---

## 41. Первый запрос к ИИ-агенту

Начни с итерации 0.

Сначала:

1. предложи минимальную структуру Maven-проекта;
2. объясни назначение каждой зависимости;
3. создай PostgreSQL-конфигурацию;
4. подключи Liquibase;
5. создай общий формат ошибок;
6. создай endpoint:

```text
GET /api/public/health
```

7. добавь один context test;
8. покажи структуру каталогов;
9. покажи команды запуска;
10. объясни, что будет добавлено позднее;
11. остановись.

На итерации 0 не реализовывать:

- Spring Security;
- пользователей;
- JWT;
- товары;
- склады;
- закупки;
- продажи;
- платежи;
- бухгалтерию;
- аудит;
- Redis;
- Kafka.

Не переходи к итерации 1 без отдельной команды.

---

## 42. Команды для продолжения

### Следующая итерация

```text
Переходи к следующей итерации.

Сначала проанализируй текущее состояние проекта и тестов.
Затем реализуй только следующий этап.
Не добавляй функциональность будущих итераций.
```

### Повторное code review

```text
Не переходи дальше.

Проведи code review текущей итерации.
Проверь безопасность, архитектуру, Liquibase, тесты,
транзакционность, ownership, финансовую и складскую целостность.

Классифицируй проблемы как P0, P1, P2 или P3.
```

### Объяснение без изменения кода

```text
Код пока не изменяй.

Подробно объясни текущий поток выполнения:
от HTTP-запроса через Spring Security и application service
до транзакции, stock movement, payment ledger и audit event.
```

### Проверка финансовой целостности

```text
Не добавляй новые функции.

Проверь payment, payment ledger, invoices и refunds на:
- double payment;
- duplicate requests;
- отсутствие idempotency;
- неправильные transaction boundaries;
- редактируемые ledger entries;
- refund больше суммы оплаты;
- доступ customer к чужим документам.
```

### Проверка складской целостности

```text
Не добавляй новые функции.

Проверь stock, reservations, shipments и returns на:
- race conditions;
- negative stock;
- double reservation;
- shipment больше reserve;
- отсутствие stock movement;
- редактируемые stock movements;
- неправильные transaction boundaries.
```

### Проверка pricing

```text
Не добавляй новые функции.

Проверь pricing и discounts на:
- доверие цене клиента;
- отсутствие price snapshot;
- nondeterministic discount order;
- expired prices;
- negative final price;
- отсутствие audit при изменении цены;
- отсутствие тестов конфликтующих скидок.
```

