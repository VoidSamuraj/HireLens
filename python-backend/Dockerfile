ARG USE_CUDA=false

# Obrazy bazowe
FROM python:3.11-slim AS cpu
FROM nvidia/cuda:13.0.1-cudnn-runtime-ubuntu24.04 AS gpu

# Zainstaluj Python, pip i venv (tylko w obrazie GPU)
RUN apt-get update && \
    apt-get install -y python3 python3-venv python3-pip git && \
    rm -rf /var/lib/apt/lists/*

# stage conditional: wybór obrazu final
FROM ${USE_CUDA:+gpu} AS final
WORKDIR /app

# utwórz virtualenv i ustaw PATH
RUN python3 -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# skopiuj requirements
COPY requirements.txt .

# zainstaluj torch w zależności od USE_CUDA
RUN if [ "$USE_CUDA" = "true" ]; then \
        pip install --no-cache-dir torch==2.2.0+cu121 --extra-index-url https://download.pytorch.org/whl/cu121 ; \
    else \
        pip install --no-cache-dir torch ; \
    fi

# zainstaluj resztę pakietów
RUN pip install --no-cache-dir -r requirements.txt

# kopiuj serwer
COPY server.py .

EXPOSE 8000
CMD ["python", "-m", "uvicorn", "server:app", "--host", "0.0.0.0", "--port", "8000", "--log-level", "debug"]