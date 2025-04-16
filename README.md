# OTP Verification Service

## Описание

Этот сервис предоставляет защиту операций с использованием одноразовых кодов (OTP), отправляемых по различным каналам:

- Email
- SMS
- Telegram
- Сохранение в файл

Сервис реализован на Java с использованием **Spring Boot**, **JDBC**, **PostgreSQL**, и разделением по ролям (USER/ADMIN) через **JWT-аутентификацию**.

---

## Как запустить проект

### 🔧 Предварительные требования

- Java 17+
- PostgreSQL 17
- Gradle
- (опционально) SMPPSim для SMS
- (опционально) Telegram Bot + chat_id

### Конфигурация

1. Создайте базу данных `otp_service` в PostgreSQL
2. Пролить скрипты из `resources/sql/create.sql`
3. Настройте `application.yml` с данными для подключения
4. Добавьте настройки в:
    - `email.properties`
    - `sms.properties`
    - `telegram.properties`

5. Соберите и запустите приложение:

```bash
./gradlew bootRun
```

---

## Как тестировать

### Шаг 1. Зарегистрируйте пользователя

```http
POST /api/auth/register
{
  "username": "user1",
  "password": "123456",
  "role": "USER"
}
```

Для администратора:
- Можно зарегистрировать **только одного**

---

### Шаг 2. Войдите и получите JWT-токен

```http
POST /api/auth/login
{
  "username": "user1",
  "password": "123456"
}
```

Ответ:
```json
{
  "token": "Bearer eyJhbGciOiJIUzI1NiIs..."
}
```

---

### Шаг 3. Генерация OTP

```http
POST /api/user/otp/generate
Authorization: Bearer <ваш_токен>

{
  "operationId": "confirm-order-42",
  "channel": "email", // или sms, telegram, file
  "destination": "user@example.com"
}
```

---

### Шаг 4. Проверка кода

```http
POST /api/user/otp/validate
Authorization: Bearer <ваш_токен>

{
  "operationId": "confirm-order-42",
  "code": "123456"
}
```

---

## Админ-функции (доступны только ADMIN)

### Изменить TTL и длину кода

```http
PUT /api/admin/config
Authorization: Bearer <токен администратора>

{
  "length": 6,
  "ttlSeconds": 180
}
```

### Получить список всех пользователей

```http
GET /api/admin/users
Authorization: Bearer <токен администратора>
```

### Удалить пользователя

```http
DELETE /api/admin/users/{username}
Authorization: Bearer <токен администратора>
```

---

## Возможности

- Регистрация и логин
- Генерация OTP-кодов
- Поддержка 4-х каналов отправки
- Валидация кодов
- Очистка просроченных кодов по расписанию

---

## 🧑‍💻 Автор

Evgenii Isupov — Java-разработчик