#!/bin/bash
set -e

# Configuración de variables de entorno para desarrollo local
export DB_USER=postgres
export DB_PASSWORD=postgres
export DB_NAME=applications
export DB_HOST=localhost
export DB_PORT=5432
export DB_SCHEMA=recruiters
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export KAFKA_INPUT_TOPIC=recruiters.applications-scored
export KAFKA_CONSUMER_GROUP=applications-service
export KAFKA_CONSUMER_OFFSET_RESET=earliest
export LOG_LEVEL=INFO

# Ejecutar la aplicación FastAPI
poetry run python -m uvicorn src.main:startup --host 0.0.0.0 --port 9000 --factory --reload
