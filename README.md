# Quarks Commerce

## 🚀 Demo Video
[Watch the demo video here](https://www.awesomescreenshot.com/video/41618188?key=92caa294bd964f6928c979851f3bd48e)


https://www.awesomescreenshot.com/video/41618188?key=92caa294bd964f6928c979851f3bd48e
---

A robust, production-ready e-commerce inventory management backend built with Java 17, Spring Boot, PostgreSQL, and Redis.

## Features
- Inventory management: create supply, reserve, cancel, query availability
- RESTful API with DTOs
- Redis caching and concurrency control
- JPA with PostgreSQL
- Docker and docker-compose support
- Integration tests

## Getting Started

### Prerequisites
- Docker & Docker Compose

### Quick Start (Docker)
```sh
git clone https://github.com/CRYPTOcoderAS/Quarks-Commerce
cd quarks-commerce
docker-compose up --build
```
The app will be available at [http://localhost:8080](http://localhost:8080)

### Local Development
- Configure `src/main/resources/application.yml` for your local DB/Redis
- Run with `./mvnw spring-boot:run`

## API Endpoints
- `POST /api/users` — Create user
- `GET /api/users/by-username/{username}` — Get user by username
- `GET /api/users/by-email/{email}` — Get user by email
- `POST /api/inventory/supply` — Create item supply
- `POST /api/inventory/reserve` — Reserve item quantity
- `POST /api/inventory/cancel` — Cancel reservation
- `GET /api/inventory/{itemId}` — Query item availability

## Postman Collection
A Postman collection is provided in `postman_collection.json`. Import it into Postman to test all endpoints easily.

## Running Tests
```sh
./mvnw test
```

## License
MIT 