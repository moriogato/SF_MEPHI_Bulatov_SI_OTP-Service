# OTP Service

Сервис для генерации и проверки одноразовых кодов (OTP) с поддержкой нескольких каналов доставки.

## Описание

Сервис обеспечивает безопасность операций с помощью временных одноразовых кодов. Поддерживает отправку кодов через:
- Email
- SMS (через эмулятор SMPP)
- Telegram
- Сохранение в файл

## Основные функции

- Регистрация и аутентификация пользователей (JWT)
- Генерация OTP кодов
- Отправка кодов через различные каналы
- Валидация OTP кодов
- Административные функции (управление пользователями, настройка OTP)
- Автоматическая очистка просроченных кодов

## Технологии

- **Java 17**
- **Spring Boot 3.3.0**
- **PostgreSQL 18** (через JDBC)
- **Maven** (сборка)
- **JWT** (аутентификация)
- **Lombok**
- **SLF4J/Logback** (логирование)

## Структура проекта
src/main/java/com/example/otpservice/  
├── config/ # Конфигурации (Security, App)  
├── controller/ # REST контроллеры  
├── dto/ # Data Transfer Objects  
├── entity/ # Сущности  
├── repository/ # DAO слой (JDBC)  
├── security/ # JWT фильтры и провайдеры  
├── service/ # Бизнес-логика  
├── exception/ # Обработчики ошибок  
└── util/ # Утилиты  

## База данных

### Таблицы

1. **users** - пользователи
   - `id`, `username`, `password`, `role`, `email`, `phone_number`, `telegram_id`

2. **otp_config** - настройки OTP (всегда 1 запись)
   - `id`, `ttl_seconds`, `code_length`

3. **otp_codes** - OTP коды
   - `id`, `code`, `operation_id`, `user_id`, `status`, `created_at`, `expires_at`
   - Статусы: `ACTIVE`, `EXPIRED`, `USED`

## Установка и запуск

### Требования

- Java 17+
- PostgreSQL 18+
- Maven 3.9+

### 1. Клонирование репозитория

```bash
git clone https://github.com/moriogato/SF_MEPHI_Bulatov_SI_OTP-Service  
cd otp-service
2. Настройка базы данных
bash
# Подключитесь к PostgreSQL
psql -U postgres

# Создайте базу данных
CREATE DATABASE otp_db;

# Выйдите из psql
\q

# Выполните скрипт создания таблиц
psql -U postgres -d otp_db -f src/main/resources/schema.sql
3. Настройка application.yml
Создайте файл src/main/resources/application.yml:

yaml
spring:
  application:
    name: otp-service
  
  datasource:
    url: jdbc:postgresql://localhost:5432/otp_db
    username: postgres
    password: your_password
    driver-class-name: org.postgresql.Driver
  
  mail:
    host: smtp.yandex.ru  # или smtp.gmail.com
    port: 465
    username: your_email@yandex.ru
    password: your_app_password
    properties:
      mail:
        smtp:
          auth: true
          ssl:
            enable: true

server:
  port: 8080

jwt:
  secret: your-secret-key-min-256-bits
  expiration: 3600000

otp:
  cleanup:
    interval: 60000
  default:
    ttl: 300
    length: 6
4. Настройка каналов доставки
Email (Yandex)
Получите пароль приложения в Яндексе:

Перейдите в Безопасность → Пароли приложений

Создайте пароль для "Почта"

Скопируйте полученный пароль

Обновите application.yml:

yaml
spring:
  mail:
    host: smtp.yandex.ru
    port: 465
    username: your_email@yandex.ru
    password: your_app_password
    properties:
      mail:
        smtp:
          auth: true
          ssl:
            enable: true
SMS (SMPP эмулятор)
Скачайте SMPPsim (эмулятор SMPP)

Запустите эмулятор на порту 2775

Настройки в application.yml уже заданы

Telegram
Создайте бота через @BotFather

Получите токен бота

Настройте в application.yml:

yaml
telegram:
  bot:
    token: YOUR_BOT_TOKEN
    chat-id: YOUR_CHAT_ID
    api-url: https://api.telegram.org/bot
5. Сборка и запуск
bash
# Сборка
mvn clean package

# Запуск
mvn spring-boot:run
Или через JAR:

bash
java -jar target/otp-service-1.0.0.jar
API
Публичные эндпоинты
Регистрация пользователя
http
POST /api/auth/register
Content-Type: application/json

{
    "username": "testuser",
    "password": "Test123!@#",
    "role": "USER",
    "email": "test@example.com",
    "phoneNumber": "+1234567890",
    "telegramId": "testuser"
}
Логин
http
POST /api/auth/login
Content-Type: application/json

{
    "username": "testuser",
    "password": "Test123!@#"
}
Ответ:

json
{
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "expiresIn": 3600000
}
Защищенные эндпоинты (требуется токен)
Генерация OTP
http
POST /api/otp/generate
Authorization: Bearer <token>
Content-Type: application/json

{
    "operationId": "payment_12345",
    "deliveryChannel": "file"  // email, sms, telegram, file
}
Валидация OTP
http
POST /api/otp/validate
Authorization: Bearer <token>
Content-Type: application/json

{
    "code": "123456",
    "operationId": "payment_12345"
}
Административные эндпоинты (только ADMIN)
Получение списка пользователей
http
GET /api/admin/users
Authorization: Bearer <admin_token>
Удаление пользователя
http
DELETE /api/admin/users/{userId}
Authorization: Bearer <admin_token>
Получение конфигурации OTP
http
GET /api/admin/otp-config
Authorization: Bearer <admin_token>
Изменение конфигурации OTP
http
PUT /api/admin/otp-config
Authorization: Bearer <admin_token>
Content-Type: application/json

{
    "ttlSeconds": 600,
    "codeLength": 8
}
Тестирование
PowerShell
powershell
# Регистрация
$body = @{
    username = "testuser"
    password = "Test123!@#"
    role = "USER"
    email = "test@example.com"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body

# Логин
$body = @{
    username = "testuser"
    password = "Test123!@#"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body

$token = $response.token

# Генерация OTP
$body = @{
    operationId = "payment_12345"
    deliveryChannel = "file"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/otp/generate" `
    -Method Post `
    -ContentType "application/json" `
    -Headers @{ Authorization = "Bearer $token" } `
    -Body $body
cURL
bash
# Регистрация
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Test123!@#","role":"USER","email":"test@example.com"}'

# Логин
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Test123!@#"}'

# Генерация OTP (с токеном)
curl -X POST http://localhost:8080/api/otp/generate \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"operationId":"payment_12345","deliveryChannel":"file"}'
Настройки
Параметры OTP
Параметр	Описание	По умолчанию
otp.default.ttl	Время жизни кода (сек)	300
otp.default.length	Длина кода	6
otp.cleanup.interval	Интервал очистки (мс)	60000
JWT
Параметр	Описание	По умолчанию
jwt.secret	Секретный ключ (мин. 256 бит)	-
jwt.expiration	Время жизни токена (мс)	3600000
