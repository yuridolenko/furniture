# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./mvnw clean package

# Run
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=AccountServiceTest

# Docker
docker build . -t furniture -f Dockerfile
docker run --name furniture -p 8080:8080 -d furniture
```

## Architecture

Spring Boot 3.1.5 REST API (Java 17) with MongoDB, following a standard layered architecture:

**Layers:** `web/` (controllers + DTOs) → `service/` → `repository/` → MongoDB

**Domain:** `Account` (customer with name/address/phone) and `Project` (furniture project tied to an account). `Project` holds embedded account info via `ProjectDTO`.

**Exceptions:** Custom `AccountNotFoundException` / `ProjectNotFoundException` map to HTTP 404.

**Config:** `src/main/resources/application.properties` — MongoDB connects to `127.0.0.1:27017`, management/actuator on port 8081.

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/account` | All accounts |
| GET | `/account/{id}` | Account by ID |
| POST | `/account` | Create account |
| GET | `/project/{id}` | Project by ID |
| GET | `/project/account/{id}` | Projects by account ID |
| POST | `/project` | Create project |

## Testing

Tests live under `src/test/java/ua/furniture/` in two packages:
- `service/` — unit tests with Mockito mocks
- `web/` — controller tests with MockMvc; `BaseControllerTest` and `WebTestConfiguration` are shared test infrastructure

## Docker / MongoDB Setup

The app requires MongoDB. For local Docker setup, both containers must share a Docker network:

```bash
docker network create furniture-network
docker run --name mongo-local -p 27017:27017 -d mongo
docker network connect furniture-network furniture
docker network connect furniture-network mongo-local
```
