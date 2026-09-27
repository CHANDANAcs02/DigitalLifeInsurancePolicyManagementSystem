# Digital Life Insurance Policy Management System

A Spring Boot enterprise-style backend implementing the Digital Life Insurance Policy Management System BRD. This version uses **PostgreSQL** instead of MySQL and intentionally does **not** implement Google SSO; authentication is implemented with **Spring Security + BCrypt + JWT + role-based authorization**.

## Included BRD requirements

- Spring Boot REST APIs
- PostgreSQL + JPA/Hibernate + Spring Data JPA
- DTOs and mapper classes
- Validation and standardized global exception handling
- BCrypt password hashing
- Spring Security with stateless JWT authentication
- CUSTOMER / ADMIN / EMPLOYEE roles
- Protected APIs and admin-only APIs
- Insurance plan CRUD
- Premium calculation and premium read APIs
- Policy creation with `PAYMENT_PENDING`
- First successful payment changes policy to `ACTIVE`
- RabbitMQ policy activation event + consumer
- RabbitMQ durable queues, DLQ/DLX, TTL, manual ACK/NACK, prefetch and publisher confirms
- Basic event idempotency handling
- Redis caching with `@Cacheable`, `@CachePut`, and `@CacheEvict`
- JMS renewal flow using ActiveMQ Classic over TCP
- Spring Batch Excel customer import
- AOP service logging/performance timing
- SLF4J logging
- Singleton, Prototype, Request and Session scope demonstration
- Duplicate customer handling
- Postman-friendly API flow

## What is intentionally not included

- Google OAuth2/OIDC SSO. The project uses JWT as the authentication mechanism requested for this version.

## Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL
- Redis
- RabbitMQ
- ActiveMQ Classic

## PostgreSQL setup

```sql
CREATE DATABASE digital_life_insurance;
```

Default local configuration:

```text
DB_URL=jdbc:postgresql://localhost:5432/digital_life_insurance
DB_USERNAME=postgres
DB_PASSWORD=root@123
```

Override those environment variables for your machine.

## Broker setup

RabbitMQ:

```text
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
```

ActiveMQ Classic:

```text
ACTIVEMQ_BROKER_URL=tcp://localhost:61616
ACTIVEMQ_USERNAME=admin
ACTIVEMQ_PASSWORD=admin
```

The JMS configuration deliberately uses `tcp://localhost:61616`, not `vm://...`, so it does not depend on the unavailable ActiveMQ VM transport.

Redis:

```text
REDIS_HOST=localhost
REDIS_PORT=6379
CACHE_TYPE=redis
```

## JWT configuration

The application uses a Base64-encoded secret:

```text
JWT_SECRET=<your-base64-secret>
JWT_EXPIRATION_MS=3600000
```

For local demonstration, a default development secret is provided. Use a new secret outside local development.

## Default admin for local testing

```text
ADMIN_EMAIL=admin@insurance.com
ADMIN_PASSWORD=Admin@12345
```

Change these values with environment variables for real use.

## Run

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Windows:

```text
mvnw.cmd clean test
mvnw.cmd spring-boot:run
```

API base URL:

```text
http://localhost:8080
```

## Authentication flow

1. `POST /api/auth/register` creates a CUSTOMER.
2. Password is stored using BCrypt, never as plain text.
3. `POST /api/auth/login` verifies email/password through Spring Security.
4. A JWT is returned with user id, email and role.
5. Send the token as `Authorization: Bearer <token>`.
6. `JwtAuthenticationFilter` validates the token and places the authenticated user into the Spring Security context.
7. Role rules protect admin/customer/employee APIs.

## Postman quick flow

### 1. Register customer

`POST /api/auth/register`

```json
{
  "email": "customer@gmail.com",
  "password": "Password@123",
  "name": "John Doe",
  "phone": "9876543210",
  "dateOfBirth": "1998-01-15"
}
```

### 2. Login customer

`POST /api/auth/login`

```json
{
  "email": "customer@gmail.com",
  "password": "Password@123"
}
```

Copy the `token` from the response.

### 3. Use JWT

In Postman -> Authorization -> Bearer Token -> paste the token.

### 4. View plans

`GET /api/plans`

### 5. Calculate premium

`POST /api/plans/calculate-premium`

```json
{
  "age": 30,
  "coverageAmount": 5000000,
  "termYears": 20
}
```

### 6. Create policy

`POST /api/policies`

```json
{
  "customerId": 1,
  "planId": 1,
  "coverageAmount": 5000000,
  "termYears": 20
}
```

Initial status:

```text
PAYMENT_PENDING
```

### 7. Pay first premium

`POST /api/payments`

```json
{
  "policyId": 1,
  "amount": 15000,
  "paymentType": "FIRST_PREMIUM"
}
```

A successful first payment changes the policy to `ACTIVE` and publishes the RabbitMQ `POLICY_ACTIVATED` event.

### 8. Admin login

Use the configured admin credentials with `POST /api/auth/login`. The JWT will contain `ADMIN` as the role.

Admin-only examples:

- `POST /api/admin/plans`
- `PUT /api/admin/plans/{id}`
- `DELETE /api/admin/plans/{id}`
- `POST /api/admin/customers/import`

A CUSTOMER token calling these endpoints receives `403 Forbidden`.

## Excel import format

The first worksheet should contain columns in this order:

```text
email | name | phone | dateOfBirth
```

Example:

```text
customer1@example.com | John | 9876543210 | 1995-01-10
customer2@example.com | Mary | 9876543211 | 1997-05-20
```

Imported customers are assigned the CUSTOMER role and receive a BCrypt-hashed initial password `ChangeMe123`. Change/reset this credential through a production-grade onboarding process before real use.

## Important endpoints

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

### Plans

- `GET /api/plans`
- `GET /api/plans/{id}`
- `POST /api/plans/calculate-premium`
- `POST /api/admin/plans`
- `PUT /api/admin/plans/{id}`
- `DELETE /api/admin/plans/{id}`

### Customers

- `GET /api/customers/{id}`
- `PUT /api/customers/{id}`
- `GET /api/customers`
- `GET /api/customers/{customerId}/policies`

### Policies

- `POST /api/policies`
- `GET /api/policies`
- `GET /api/policies/{id}`
- `POST /api/policies/{id}/renew`

### Payments / premiums

- `POST /api/payments`
- `GET /api/payments/{id}`
- `GET /api/policies/{policyId}/payments`
- `GET /api/policies/{policyId}/premiums`
- `GET /api/premiums/{id}`

### Batch

- `POST /api/admin/customers/import`

### Scope demonstration

- `GET /api/demo/scopes`

## Architecture

```text
Postman / Client
      |
      v
Spring Security -> JWT Filter -> Role Authorization
      |
      v
REST Controllers
      |
      v
AOP Logging
      |
      v
Services
  |       |       |       |
 JPA    Redis   RabbitMQ  JMS
  |       |       |       |
PostgreSQL     Notification  Renewal

Excel -> Spring Batch -> Customer DB
```
