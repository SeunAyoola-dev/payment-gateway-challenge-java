# Checkout.com Payment Gateway Challenge

This repository contains a Spring Boot implementation of a Payment Gateway, designed to allow merchants to securely process card payments and retrieve transaction details.

## 🚀 Getting Started

### 1. Prerequisites
- **JDK 17**
- **Docker** & Docker Compose

### 2. Start the Bank Simulator
The bank simulator must be running to process payments.
```bash
docker-compose up
```

### 3. Run the Application
```bash
./gradlew bootRun
```
The application will start on port `8090`.

### 4. Run Tests
```bash
./gradlew test
```

## 📖 API Documentation


### Primary Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/payment` | Process a card payment |
| `GET` | `/payment/{id}` | Retrieve details of a previous payment |

## 🏗️ Architecture & Design Decisions

This project follows a **Layered Architecture** (Controller-Service-Repository) and adheres to industry-standard decoupling and security practices.

Key decisions and assumptions (including PCI-DSS compliance and validation strategies) are documented in the **[DESIGN.md](./DESIGN.md)** file as an Architecture Decision Record (ADR).

## 🛡️ Security & Compliance
- **Data Masking:** Full credit card numbers are never stored. Only the last four digits are persisted for reconciliation.
- **Fail-Fast Validation:** All incoming requests are validated for format and business logic (e.g., expiry date) before being sent to the bank.
- **Error Handling:** Semantic HTTP status codes (like `503 Service Unavailable`) are used to signal transient dependency failures to merchants.