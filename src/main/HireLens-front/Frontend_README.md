# Frontend (React) 🚀

![React](https://img.shields.io/badge/React-19.1.1-blue?logo=react\&logoColor=white)
![Redux](https://img.shields.io/badge/Redux-5.0.1-purple?logo=redux\&logoColor=white)
![Nivo](https://img.shields.io/badge/Nivo-0.99.0-orange)

## Description

The frontend displays interactive job offer lists, allows filtering by category and technologies, and shows data visualizations (pie, treemap, geo) using Nivo. Supports real-time notifications via WebSockets and multilingual interface via i18next.

All requests to fetch job data start with a **REST API call** to initiate a task and receive a unique task ID. Once the task is created, the frontend opens a **WebSocket connection** to receive live updates about data processing status and results.


## Pages 🖥️
The frontend has three main pages:
1. **Jobs List** – displays all job offers with filtering options.
2. **Job Details** – detailed view of a selected job offer.
3. **Dashboard** – interactive charts and visualizations (pie, treemap, geo).

## Setup 🔨 – API URL

📌 **Where to set the API URL:**

| Source             | How it is set                                                                           | When it is used                                                                                       |
| ------------------ | --------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------- |
| **Frontend .env**  | `src/main/HireLens-front/.env` with `REACT_APP_API_URL`                                 | Used if building/running frontend locally with npm; Gradle task reads this if Docker ARG not provided |
| **Gradle build**   | `build.gradle` reads `.env` or system environment; sets `VITE_API_URL`                  | Used during `npmRunBuild` task to inject API URL into frontend build                                  |
| **Docker Compose** | `environment` section of service, e.g., `REACT_APP_API_URL: http://spring-backend:8082` | Overrides value when building container if ARG/ENV is passed; used by Gradle if properly forwarded    |

**Priority order:** Docker Compose > Gradle `.env` / system env > Frontend local `.env`


## Run ⚡

**Install**

```bash
npm install
```

**Run**

```bash
npm run dev
```

**or build**

```bash
npm run build
```

## Backend Connection ⚡
- Development: configure `.env` with `REACT_APP_API_URL` pointing to your Spring Boot backend.
- Production (Docker/Spring Boot hosting): frontend is served by Spring Boot and API URLs should point to the deployed backend.

## Structure 🗂

* `src/components/` – UI components
* `src/pages/` – views
* `src/services/` – API and WebSocket calls
* `src/i18n/` – translation configuration

## Libraries 📦

* React, Redux Toolkit, React Router
* Axios, i18next, react-i18next
* Nivo (pie, treemap, geo), GSAP
* STOMP + SockJS

## Communication Flow 🔄

1. Frontend sends **REST request** to backend to create a new job processing task.
2. Backend returns a **task ID**.
3. Frontend opens a **WebSocket connection** and subscribes to updates for that task ID.
4. Updates include **data processing progress, partial results, and final status**.

This approach allows the UI to remain responsive and display live feedback while backend processes the data asynchronously.
