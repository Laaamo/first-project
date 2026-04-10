# 🎓 UGA Event Finder

**Duration:** Jan 2025 — Present  
**Stack:** Java, Spring Boot, PostgreSQL, REST APIs, JUnit

## Overview

A Java Spring Boot web application that aggregates UGA campus events and exposes a RESTful API for searching and filtering them by date, category, and location. Built with a clean MVC architecture and backed by PostgreSQL.

## Features

- 📅 Aggregates **150+ campus events** from multiple sources
- 🔍 Search and filter by **date**, **category**, and **location**
- 🌐 RESTful API with clean endpoint design
- 🗄️ PostgreSQL backend with normalized schema
- ✅ JUnit tests covering all CRUD operations

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/events` | List all events |
| GET | `/api/events?date=2025-02-14` | Filter by date |
| GET | `/api/events?category=academic` | Filter by category |
| GET | `/api/events?location=tate` | Filter by location |
| GET | `/api/events/{id}` | Get event by ID |
| POST | `/api/events` | Create new event |
| PUT | `/api/events/{id}` | Update event |
| DELETE | `/api/events/{id}` | Delete event |

## Architecture

```
src/
├── main/java/com/bryanperez/ugaevents/
│   ├── controller/     # REST controllers (MVC)
│   ├── service/        # Business logic layer
│   ├── repository/     # Spring Data JPA repositories
│   ├── model/          # JPA entity classes
│   └── dto/            # Data transfer objects
└── test/               # JUnit test suite
```

## Database Schema

```sql
CREATE TABLE events (
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    category    VARCHAR(100),
    location    VARCHAR(255),
    event_date  DATE NOT NULL,
    event_time  TIME,
    created_at  TIMESTAMP DEFAULT NOW()
);
```

## Running Locally

```bash
# Prerequisites: Java 17+, PostgreSQL, Maven

# 1. Set up database
psql -U postgres -c "CREATE DATABASE uga_events;"

# 2. Configure application.properties
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Edit DB credentials

# 3. Build and run
mvn spring-boot:run

# 4. Run tests
mvn test
```

## Key Takeaways

- End-to-end Spring Boot application from data layer to REST API
- Clean separation of concerns with MVC + service layer pattern
- Real PostgreSQL schema design and JPA entity mapping
- Test-driven approach with JUnit for data integrity
