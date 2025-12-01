# Bankcards — REST API for Bank Card Management

## Описание

Bankcards — backend‑приложение на Spring Boot для управления банковскими картами, переводов между своими картами и работы с пользователями.  
Позволяет реализовать: создание/удаление карт, блокировка, активация, переводы, просмотр баланса, блокировки карт, soft‑delete, при этом обеспечивая безопасность через JWT + роли (USER / ADMIN).

## Основные функции

### Для обычного пользователя (USER)
- Просмотр своих карт (с фильтрацией / пагинацией)  
- Запрос блокировки карты  
- Переводы между своими картами  
- Просмотр баланса  
- Карты возвращаются с маскированным номером (например, `**** **** **** 1234`), действующим сроком и статусом  

### Для администратора (ADMIN)
- Создание новых карт для любого пользователя  
- Блокировка / активация / удаление (soft‑delete) карт  
- Управление пользователями (смена роли, soft-delete)  
- Просмотр всех карт системы с фильтрацией и пагинацией
- Управление запросами на блокировку карт (подтвердить, отказать)
  
### Доп функция
- Запланированная задача (Scheduler) для автоматической проверки: при истечении срока действия карты — статус меняется на EXPIRED

## Структура карты

- **Номер карты** — хранится зашифрованным, в ответах возвращается замаскированным
- **last4** — последние 4 цифры банковской карты
- **Owner Name** — имя владельца  
- **Expiry Date** — месяц/год окончания срока действия  
- **Status** — `ACTIVE`, `BLOCKED`, `EXPIRED`  
- **Balance** — баланс карты  

Soft‑delete реализован через поле `deleted`, чтобы можно было «удалять» без физического удаления из БД.

## Технологический стек

- Java 21 / Spring Boot  
- Spring Data JPA + Hibernate  
- Spring Security + JWT  
- PostgreSQL + Liquibase для миграций  
- MapStruct & Lombok
- Docker & Docker Compose
- OpenAPI для документации API  

## Как запустить локально

1. Клонируйте репозиторий:  
   ```bash
    git clone https://github.com/GoylikDmitriy/bankcards.git
    
2. Настройте подключение к БД в .env PostgreSQL, например:
   ```
   POSTGRES_DB=bankcards
   POSTGRES_USER=postgres
   POSTGRES_PASSWORD=1234
   PGDATA=/data/postgres-bankcards
    
   SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-bankcards:5432/bankcards
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=1234
   SPRING_PROFILES_ACTIVE=docker
   
3. Поднимите docker-compose.yml:
   ```
   docker compose up
   
4. После поднятия контейнеров API документация будет доступно по адресу:
   ```
   http://localhost:8080/swagger-ui.html

5. При запуске приложения будут автоматически созданы необходимые таблицы. Также будут добавлены исходные данные:
   Два пользователя USER и ADMIN. Логин и пароль для входа:
   ```
   USER: email: user@mail.com
         password: 12345678
   
   ADMIN: email: admin@mail.com
          password: 12345678
   
  Также будут добавлены две банковские карты для этого пользователя.
