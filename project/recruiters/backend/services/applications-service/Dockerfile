FROM python:3.10-slim

WORKDIR /app

# Instalar dependencias del sistema
RUN apt-get update && apt-get install -y \
    build-essential \
    librdkafka-dev \
    && rm -rf /var/lib/apt/lists/*

# Instalar Poetry
RUN pip install poetry==2.1.1

# Configurar Poetry para no crear un entorno virtual
RUN poetry config virtualenvs.create false

# Copiar proyecto
COPY . .

# Instalar dependencias
RUN poetry install --no-interaction

# Variables de entorno por defecto
ENV DB_HOST=localhost \
    DB_PORT=5432 \
    DB_USER=test \
    DB_PASSWORD=test \
    DB_NAME=test \
    DB_SCHEMA=recruiters \
    KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
    KAFKA_CONSUMER_GROUP=application-scoring-service \
    KAFKA_CONSUMER_OFFSET_RESET=earliest \
    KAFKA_INPUT_TOPIC=recruiters.application-analysed-events \
    LOG_LEVEL=INFO

# Puerto para FastAPI
EXPOSE 8000

# Script para iniciar FastAPI y Kafka consumer en paralelo
RUN echo '#!/bin/bash\n\
python -m uvicorn src.infrastructure.app.app:app --host 0.0.0.0 --port 8000 & \n\
python -m src.main\n\
wait\n' > /app/start.sh && chmod +x /app/start.sh

# Comando para iniciar tanto FastAPI como el consumidor de Kafka
CMD ["/app/start.sh"]