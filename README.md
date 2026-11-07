# Card Management

A Spring Boot application for managing ID cards. It lets you issue, edit, delete, search and filter cards, and check whether a card is active, expired or blocked. Card numbers are generated with a check digit (Luhn algorithm), like on real documents. Data is stored in MySQL and everything is available through a REST API.

## Features

- Issue, edit and delete cards (name, ID number, department, issue date, expiry date, status)
- Validity check: active, expired or blocked
- Search by name, department or ID number
- Filter by name, issue year, expiry year and status (all filters optional and combinable)
- ID numbers generated automatically with a check digit, and validated when a card is added with an existing ID
- REST API returning JSON

## Tech stack

- Java 21
- Spring Boot 4.1.2 (Spring Web MVC, Spring Data JPA)
- MySQL (XAMPP + phpMyAdmin)
- Maven

## Getting started

### Prerequisites

- JDK 21
- XAMPP with MySQL
- IntelliJ IDEA (or any IDE with Maven support)

### 1. Create the database

Start **MySQL** (and **Apache**, for phpMyAdmin) in the XAMPP Control Panel. Open `http://localhost/phpmyadmin`, click **New** and create an empty database named `demo`. Tables are created automatically on first run.

### 2. Configure the connection

`src/main/resources/application.properties`:

```
spring.application.name=demo
spring.datasource.url=jdbc:mysql://localhost:3306/demo
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

XAMPP's `root` user has an empty password by default. Change the values if your setup differs.

### 3. Run

Open the project in IntelliJ, reload Maven, and run `DemoApplication`. Or from the terminal:

```
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`.

## Card ID numbers

An ID is 7 digits: 6 random digits followed by 1 check digit calculated with the Luhn algorithm. A mistyped ID can be detected without looking it up in the database.

Example: `123456` produces check digit `6`, so `1234566` is valid and `1234567` is not.

## REST API

Base path: `/kartice`

| Method | URL | Description |
|---|---|---|
| POST | `/kartice` | Issue a new card (ID is generated) |
| GET | `/kartice` | List cards, optional filters `ime`, `godinaIzdavanja`, `godinaIsteka`, `status` |
| GET | `/kartice/pretraga?q=...` | Search by name, department or ID number |
| GET | `/kartice/{id}` | Get one card |
| PUT | `/kartice/{id}` | Edit a card |
| DELETE | `/kartice/{id}` | Delete a card (204 if deleted, 404 if not found) |
| GET | `/kartice/{id}/valjanost` | Check whether the ID is valid and whether the card is active, expired or blocked |

Errors such as "Kartica ne postoji." are returned as `400` with a JSON body: `{"greska": "..."}`.

### Request body (POST and PUT)

```json
{
  "ime": "Ana Horvat",
  "odjel": "IT",
  "datumIzdavanja": "2025-03-01",
  "datumIsteka": "2027-03-01",
  "status": "AKTIVNA"
}
```

Dates use the format `yyyy-MM-dd`. Status is `AKTIVNA` or `BLOKIRANA`. A card also counts as expired once its expiry date is in the past.

### Examples

```
GET http://localhost:8080/kartice?status=AKTIVNA
GET http://localhost:8080/kartice?ime=an&godinaIzdavanja=2025&godinaIsteka=2027
GET http://localhost:8080/kartice/pretraga?q=IT
GET http://localhost:8080/kartice/1234566/valjanost
```

POST, PUT and DELETE can be tested with Postman or PowerShell:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/kartice -ContentType "application/json" -Body '{"ime":"Ana Horvat","odjel":"IT","datumIzdavanja":"2025-03-01","datumIsteka":"2027-03-01","status":"AKTIVNA"}'
```

## Project structure

```
src/main/java/com/example/demo
├── DemoApplication.java      Entry point (also runs a demo test of all features)
├── Kartica.java              JPA entity (card data)
├── KarticaRepository.java    Spring Data JPA repository
├── KarticaManager.java       Business logic: add, edit, delete, search, filter, validity
├── KarticaController.java    REST endpoints
└── IdBroj.java               ID generation and check digit validation
```

## Notes

- `DemoApplication` currently runs a test routine on startup that **deletes all cards** from the `kartica` table and creates sample ones. Remove that code and keep only `SpringApplication.run(...)` before storing real data.
- Spring Security is not included, so all endpoints are open.
