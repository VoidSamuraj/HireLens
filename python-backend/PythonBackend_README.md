# Job Offer Analyzer API 🤖

![FastAPI](https://img.shields.io/badge/FastAPI-0.101.0-green?logo=fastapi\&logoColor=white)
![Python](https://img.shields.io/badge/Python-3.11-blue?logo=python\&logoColor=white)
![PyTorch](https://img.shields.io/badge/PyTorch-2.2-red?logo=pytorch\&logoColor=white)
![HuggingFace](https://img.shields.io/badge/HuggingFace-Transformers-orange?logo=huggingface\&logoColor=white)
![SentenceTransformers](https://img.shields.io/badge/SentenceTransformers-2.2-purple)

## Description

The AI backend analyzes job descriptions and extracts:

* **Seniority level** (intern, junior, mid, senior)
* **Technical skills** with importance levels (1–5)
* **Deduplicated and semantically merged skill names**

It uses:

* **HuggingFace transformers** for NER and reasoning (LLM)
* **SentenceTransformers** for skill embeddings and semantic similarity
* **FastAPI + Pydantic** for REST API interface
* **ThreadPoolExecutor** for sequential LLM processing

## Run ⚡

### Locally

```bash
pip install -r requirements.txt
uvicorn server:app --reload --host 0.0.0.0 --port 8000
```

### Docker

* Build and run CPU version:

```bash
docker build --build-arg USE_CUDA=false -t job-analyzer .
docker run -p 8000:8000 -e HF_TOKEN=$HF_TOKEN job-analyzer
```

* Build and run GPU version:

```bash
docker build --build-arg USE_CUDA=true -t job-analyzer .
docker run --gpus all -p 8000:8000 -e HF_TOKEN=$HF_TOKEN job-analyzer
```

* Using Docker Compose (example):

```yaml
  python-backend:
    build:
      context: ./python-backend
      args:
        USE_CUDA: "true"  # <-- change to "false" if you don't have CUDA
    container_name: python-backend
    ports:
      - "8000:8000"
    env_file:
      - ./python-backend/.env
    environment:
      - USE_CUDA=true
      - HF_HOME=/models
    volumes:
      - ./python-backend/models:/models
    deploy:
      resources:
        reservations:
          devices:
            - driver: nvidia
              count: all
              capabilities: [ gpu ]
```

## Structure 🗂

* `models/` – Pydantic models and AI logic (skill extraction, LLM queries, embeddings, grouping)

## API Endpoints 🌐

* `POST /analyze` – analyze a single job offer
  - **Request:** `{ "text": "Job description text..." }`
  - **Response:** `{ "seniority": "mid", "skills": { "Backend": {"Java": 5, "Spring": 4}, ... } }`
  - Uses LLM + NER + embeddings to extract skills, assign levels, and group them hierarchically.

- **POST /groupSkills** – Group a dictionary of skills into hierarchical categories.
    - **Request body:** `{ "skills": { "Java": 5, "Spring Boot": 4, "React": 4, ... } }`
    - **Response:** `{ "Backend": { "Java": 5, "Spring Boot": 4 }, "Frontend": { "React": 4 }, ... }`
    - Input: a flat skill → level dictionary.
    - Output: grouped skill → level dictionary by category using LLM.

## Dependencies 📦

* **Python packages**: FastAPI, Pydantic, torch, transformers, sentence-transformers, huggingface_hub
* **Optional hardware**: CUDA-enabled GPU for faster LLM inference

## Processing Flow 🔄

1. **Frontend submits a job description** to `/analyze`.
2. **NER model** extracts raw skill candidates.
3. **LLM** determines seniority and enriches skill extraction.
4. **Technical skill filtering**: semantic embeddings remove irrelevant or non-technical phrases.
5. **Skill level assignment**: each skill gets importance level 1–5.
6. **Semantic merging**: similar skills are deduplicated and canonicalized.
7. **Hierarchical grouping**: skills are categorized using the LLM.
8. **Result returned** to the frontend for display or further processing.

This setup ensures accurate skill extraction, responsive API, and optional GPU acceleration.
