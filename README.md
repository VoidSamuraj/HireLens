# Job Offers Platform 🚀

![React](https://img.shields.io/badge/React-19.1.1-blue?logo=react\&logoColor=white) ![Redux](https://img.shields.io/badge/Redux-5.0.1-purple?logo=redux\&logoColor=white) ![Nivo](https://img.shields.io/badge/Nivo-0.99.0-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen?logo=springboot\&logoColor=white) ![WebFlux](https://img.shields.io/badge/WebFlux-3.3.3-009688?logo=spring\&logoColor=white) ![Flyway](https://img.shields.io/badge/Flyway-9.21.1-e95420?logo=flyway\&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-Enabled-lightblue?logo=websocket\&logoColor=white) ![Lombok](https://img.shields.io/badge/Lombok-1.18.28-dc322f?logo=java\&logoColor=white) ![JSoup](https://img.shields.io/badge/JSoup-1.21.2-ff9800?logo=java\&logoColor=white)
![SLF4J](https://img.shields.io/badge/SLF4J-2.0.12-black?logo=java\&logoColor=white) ![Logback](https://img.shields.io/badge/Logback-1.5.19-brown?logo=logstash\&logoColor=white)
![Python](https://img.shields.io/badge/Python-3.11-blue?logo=python\&logoColor=white) ![FastAPI](https://img.shields.io/badge/FastAPI-0.101.0-green?logo=fastapi\&logoColor=white) ![PyTorch](https://img.shields.io/badge/PyTorch-2.2-red?logo=pytorch\&logoColor=white)
![HuggingFace](https://img.shields.io/badge/HuggingFace-Transformers-orange?logo=huggingface\&logoColor=white) ![SentenceTransformers](https://img.shields.io/badge/SentenceTransformers-2.2-purple) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql\&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-24-blue?logo=docker\&logoColor=white) ![CUDA](https://img.shields.io/badge/CUDA-13.0-red?logo=nvidia\&logoColor=white)

---

## Description

Complete job offers processing platform with AI-enhanced skill extraction and real-time updates.:

* **React Frontend**: interactive job lists, filtering, dashboards (pie, treemap, geo)
* **Spring Boot Backend**: REST API, WebSocket live updates, external API integration
* **AI FastAPI (Python)**: skill extraction, seniority analysis, deduplication, hierarchical grouping
* **PostgreSQL**: job storage, aggregation, filtering, migrations with Flyway
* **Docker Compose**: full containerized deployment with optional GPU acceleration

---

## Motivation & Purpose 🎯

I built this **Job Offers Processing Platform** to gain hands-on experience with **Spring Boot**, **OpenAPI** and multiple APIs, **Docker**, and **AI/ML integration**. The project showcases **AI-driven skill extraction**, **seniority analysis**, and **real-time updates** via WebSockets.
This project demonstrates modern backend development, API design, containerization, and AI-enhanced data processing in a cohesive, production-relevant platform.

---

## Architecture Overview 📊

The system consists of three main layers: **Frontend (React)**, **Backend (Spring Boot)**, and **AI Backend (FastAPI)**, with **PostgreSQL** as the database and full containerization via **Docker Compose**.

**Diagrams:**

* **Component Diagram** – shows connections between frontend, backend, and AI.
<div style="background-color: white; display: inline-block; padding: 10px; margin-bottom:20px;">
    <img src="./diagrams/AI_Spring_Components.svg" alt="Component Diagram">
</div>

* **Deployment Diagram** – illustrates container layout, networks, and database placement.
<div style="background-color: white; display: inline-block; padding: 10px; margin-bottom:20px;">
    <img src="./diagrams/AI_Spring_Deployment.svg" alt="Deployment Diagram">
</div> 

* **Class Diagram** – key backend entities and their relationships.
<div style="background-color: white; display: inline-block; padding: 10px; margin-bottom:20px;">
    <img src="./diagrams/AI_Spring_Class.svg" alt="Class Diagram">
</div> 

* **Sequence Diagram** – flow of job offer analysis and inter-component communication.
<div style="background-color: white; display: inline-block; padding: 10px;">
    <img src="./diagrams/AI_Spring_Sequence.svg" alt="Sequence Diagram">
</div> 

- [Frontend Details](./src/main/HireLens-front/Frontend_README.md)
- [Spring Backend Details](./src/main/java/com/voidsamuraj/HireLens/Backend_README.md)
- [Python AI Backend Details](./python-backend/PythonBackend_README.md)

---

## Features ✨

* **Job analysis**: seniority, technical skills with levels (1–5), semantic deduplication and grouping
* **Frontend**: job lists, filtering, interactive dashboards, multilingual support
* **Backend**: REST API endpoints, WebSocket STOMP for live updates, AI backend integration
* **AI Backend**: `/analyze` for skill extraction, `/groupSkills` for hierarchical grouping
* **Database**: PostgreSQL storage with real-time aggregation and filtering
* **Docker**: containerized services, GPU support for AI acceleration

---

## Processing Flow 🔄

1. Frontend sends **REST request** → Spring Boot (`/api/startJob`) → receives task ID
2. Backend retrieves offers from PostgreSQL, filters, and **pushes initial results via WebSocket**
3. Backend fetches new job offers from external APIs in batches
4. AI FastAPI analyzes each batch: skill extraction, seniority, semantic filtering
5. Deduplication + hierarchical grouping of skills (`/groupSkills`)
6. Results stored in PostgreSQL
7. Backend pushes **live updates** to frontend in real-time

---

## Structure 🗂

<pre style="font-family: monospace; line-height: 1.3;">
📁 HireLens/
├─ 📁 <b>src/</b>
│  └─ 📁 main/
│     ├─ 📁 <b>HireLens-front/</b>  React frontend
│     │  └─ 📄 <b>.env/</b>          Contains REACT_APP_API_URL
│     ├─ 📁 <b>java/</b>            Spring Boot backend
│     └─ 📁 resources/
│        ├─ 📄 <b>V1__create_job_entity_with_tsvector.sql</b>  Init file for PostgreSQL
│        └─ 📄 <b>application.yml</b>  API configuration
├─ 📁 <b>python-backend/</b>        AI backend (FastAPI)
│  └─ 📁 models/                     Pydantic models and AI logic
├─ 📁 pgdata/                        Postgres data volume
├─ 📄 <b>docker-compose.yml</b>     Docker Compose setup
├─ 📁 diagrams/                      Class, Component, Sequence, Deployment diagrams
└─ 📄 <b>.env</b>                    Environment variables (e.g., Hugging Face token HF_TOKEN)
</pre>

---


## Setup 🔨

**application.yml**
📄 `HireLens/src/main/resources/application.yml`

```
spring:
  application:
    name: Job Requirements Analyzer

  config:
    import: classpath:database.yml

adzuna:
  api:
    id: [YOUR_ADZUNA_API_ID]
    key: [YOUR_ADZUNA_API_KEY]

ai-server:
  address: http://python-backend:8000

```
**database.yml**
📄 `HireLens/src/main/resources/database.yml`

```
# You may want to change setings for Postgres, Flyway, Database Connection etc
```


**.env (HuggingFace Token)**
📄 `python-backend/.env`

```
HF_TOKEN=[YOUR_TOKEN]
```

**docker-compose.yml**
📄 `docker-compose.yml`

```
# Set AI container variables
- HF_TOKEN=${HF_TOKEN}
- USE_CUDA=true
- HF_HOME=/models
```
### If you wanna use different address, make sure there is provided correct api address(server)

| Source             | How it is set                                                                                       | When it is used                                                                                |
| ------------------ | --------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------- |
| **Frontend .env**  | `src/main/HireLens-front/.env` with `REACT_APP_API_URL`                                             | Used when running or building frontend locally with npm; fallback if no higher source provided |
| **Gradle build**   | Reads from `.env` or system environment; sets `VITE_API_URL` in the frontend build task             | Used during Gradle `npmRunBuild` to inject the API URL into frontend code                      |
| **Docker Compose** | `environment` section in `docker-compose.yml`, e.g. `REACT_APP_API_URL: http://spring-backend:8082` | Overrides all other sources when building/running containers                                   |

> **Priority order:** Docker Compose → Gradle ENV → Frontend `.env`
>
> Only one source applies at runtime; higher-priority sources override lower ones.

```
REACT_APP_API_URL=http://localhost:8080
```
### If you build whole app (Spring), make sure there is proper address in 
**PostgreSQL pg_hba.conf**
📄 `init-scripts/pg_hba.conf`

```
# Adjust access rules for Docker/local network
host    all             all             172.18.0.0/16           md5
```


---

## Run ⚡

**Frontend:**

Install
```bash
npm --prefix src/main/HireLens-front install
```

Run

```bash
npm --prefix src/main/HireLens-front run dev
```

or build

```bash
npm --prefix src/main/HireLens-front run build
```

**Spring Boot Backend:**

```bash
./gradlew bootRun
```

**Python AI Backend:**

```bash
pip install -r ./python-backend/requirements.txt
uvicorn server:app --reload --host 0.0.0.0 --port 8000 --app-dir ./python-backend
```

**Docker Compose (full environment):**

```bash
docker-compose up --build
```

---

## Tech Stack 📦

* **Frontend:** React, Redux, Axios, Nivo, GSAP, i18next, STOMP + SockJS
* **Backend:** Spring Boot 3, WebFlux, WebSockets, Flyway, JPA/Hibernate, Lombok, JSoup, PostgreSQL
* **AI:** Python 3.11, FastAPI, Pydantic, PyTorch 2.2, HuggingFace Transformers, SentenceTransformers
* **Database:** PostgreSQL 16
* **DevOps:** Docker, Docker Compose, optional CUDA GPU acceleration
