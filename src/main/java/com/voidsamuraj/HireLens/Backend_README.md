# Backend (Spring Boot) 🖥️

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen?logo=springboot&logoColor=white)
![WebFlux](https://img.shields.io/badge/WebFlux-3.3.3-009688?logo=spring&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-9.21.1-e95420?logo=flyway&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-Enabled-lightblue?logo=websocket&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-1.18.28-dc322f?logo=java&logoColor=white)
![JSoup](https://img.shields.io/badge/JSoup-1.21.2-ff9800?logo=java&logoColor=white)
![SLF4J](https://img.shields.io/badge/SLF4J-2.0.12-black?logo=java&logoColor=white)
![Logback](https://img.shields.io/badge/Logback-1.5.19-brown?logo=logstash&logoColor=white)

## Description
The backend provides core business logic, API endpoints, and data persistence:
- Exposes **REST API** for frontend integration
- Uses **WebSockets** for live data updates (task progress, job status)
- Integrates with external job APIs (Adzuna, Remotive, Joinrise)
- Connects to an **AI module** (Python FastAPI) for skills extraction
- Stores and manages data in **PostgreSQL**
- Handles **database migrations** using Flyway

## Run ⚡
**(IN MAIN DIRECTORY)**
* Locally: 
```bash
./gradlew bootRun
```
* In Docker: 
```bash
    docker-compose up
```

## Dependencies 📦

* **Spring Boot Starters**: Web, WebFlux, WebSockets, Actuator
* Spring Data JPA, Hibernate
* Flyway (PostgreSQL)
* H2 (for testing)
* SLF4J + Logback
* JSoup, Apache Commons Text
* Lombok
* Testing: Spring Boot Test, Testcontainers,
* PostgreSQL JDBC Driver

## Structure 🗂

* `config/` – API clients and WebSocket configuration
* `controllers/` – REST endpoints
* `dto/` – data transfer objects (external API, aggregation, AI)
* `entity/` – database entities (`JobEntity`, `SkillEntity`, etc.)
* `mapper/` – mapping between DTOs and entities
* `repository/` – JPA repositories
* `service/` – business logic, AI integration, API orchestration
* `util/` – utility classes (e.g., `DateParser`)

## Routes / Endpoints 🚦
| Method | Path | Description | Request body | Response |
|--------|------|-------------|--------------|----------|
| POST | `/api/startJob` | Start a job processing task | `StartJobPayload` | `{ "jobId": "<uuid>" }` |
| POST | `/api/stopJob` | Stop a running task | `UUID` | `HTTP 200` |

**WebSocket (STOMP)**
- Endpoint: `/ws`
- Broker: `/dataUpdate`
- Used for pushing live updates on task progress, partial results, and final status.

## Communication & Processing Flow 🔄
1. **Frontend search request**: The user initiates a search for jobs.
2. **Database check**: Backend retrieves matching job offers from PostgreSQL, aggregates and filters them, then pushes the initial results to the frontend via WebSocket.
3. **Live API fetching**: Backend begins fetching job offers from external APIs in batches.
4. **AI processing**: Each batch is sent to the AI module for skills extraction, analysis, and transformation.
5. **Data aggregation & storage**: Processed results are aggregated and stored back into PostgreSQL.
6. **Live frontend updates**: Using WebSockets, the backend pushes processed offers and progress updates to the frontend in real-time.
7. **Continuation or stop**: The process continues until all external offers are processed or the user performs a new search / stops the task.

This design allows:
- Responsive UI with real-time feedback
- Incremental data processing without blocking the frontend
- Scalable integration with external APIs and AI services