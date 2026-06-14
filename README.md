# Оптово-розничный магазин стройматериалов (BuildStore)

Учебный проект по разработке backend-системы для управления магазином строительных материалов. Фокус проекта — на безопасности, финансовой целостности, складском учете и архитектуре Modular Monolith.

## 🚀 Технологический стек
- **Java 21**
- **Spring Boot 3.2.5** (Web, Data JPA, Security, Actuator, Validation)
- **PostgreSQL**
- **Liquibase** (миграции БД)
- **Lombok & MapStruct**
- **Testcontainers & H2** (для тестирования)
- **Maven**

## 🏗 Архитектура
Проект придерживается стиля **Modular Monolith** и организации кода **package-by-feature**. Основные модули включают:
- `auth` & `security`: Инфраструктура доступа и JWT.
- `inventory` & `warehouse`: Складские остатки и движение товара.
- `sales` & `pricing`: Заказы, прайс-листы и скидки.
- `accounting` & `payment`: Бухгалтерия и платежи.
- `audit`: Журналирование критических операций.

## 🛠 Установка и запуск

### Требования
- JDK 21
- Maven 3.9+
- PostgreSQL (локально или в Docker)

### Запуск приложения
1. Склонируйте репозиторий.
2. Настройте переменные окружения (или используйте значения по умолчанию в `application.yml`):
   - `DATABASE_URL`
   - `DATABASE_USERNAME`
   - `DATABASE_PASSWORD`
3. Соберите проект:
   ```bash
   mvn clean install
   ```
4. Запустите:
   ```bash
   mvn spring-boot:run
   ```

### Тестирование
```bash
mvn clean test
```

## 📝 Документация
Подробная документация по итерациям и архитектуре находится в директории `docs/`:
- [PROJECT_CONTEXT.md](docs/PROJECT_CONTEXT.md) — Текущий статус и план.
- [DOCUMENTATION.md](docs/DOCUMENTATION.md) — API и форматы данных.
- [CHANGELOG.md](docs/CHANGELOG.md) — История изменений.
- [UML.md](docs/UML.md) — Структура классов и паттерны.

## 🛡 Принципы разработки
- **Финансовая дисциплина**: Использование `BigDecimal`, неизменяемые леджеры, запрет на удаление проводок.
- **Складская целостность**: Работа через `StockMovement`, запрет на отрицательные остатки.
- **Безопасность**: Принцип ownership (проверка владения ресурсом), RBAC (Role-Based Access Control).
- **Идемпотентность**: Поддержка `Idempotency-Key` для критических операций.
