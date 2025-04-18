# Build stage
FROM python:3.10-slim AS builder

WORKDIR /app

# Instalar dependencias del sistema necesarias para la compilación
RUN apt-get update && apt-get install -y \
    build-essential \
    librdkafka-dev \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Instalar Poetry
ENV POETRY_HOME=/opt/poetry
ENV POETRY_VERSION=1.7.1
RUN curl -sSL https://install.python-poetry.org | POETRY_HOME=/opt/poetry python3 - && \
    cd /usr/local/bin && \
    ln -s /opt/poetry/bin/poetry && \
    poetry config virtualenvs.create false

# Copiar solo los archivos necesarios para la instalación de dependencias
COPY pyproject.toml poetry.lock ./

# Instalar dependencias
RUN poetry install --no-interaction --no-root --only main

# Final stage
FROM python:3.10-slim

WORKDIR /app

# Instalar solo las dependencias de runtime necesarias
RUN apt-get update && apt-get install -y \
    librdkafka1 \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copiar las dependencias instaladas desde el builder
COPY --from=builder /usr/local/lib/python3.10/site-packages /usr/local/lib/python3.10/site-packages

# Crear un usuario no root
RUN useradd -m -u 1000 appuser

# Copiar el código de la aplicación
COPY --chown=appuser:appuser ./src ./src

# Cambiar al usuario no root
USER appuser

# Script de inicio
COPY --chown=appuser:appuser docker-entrypoint.sh /app/
RUN chmod +x /app/docker-entrypoint.sh

# Healthcheck
#HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
#    CMD curl -f http://localhost:8000/ || exit 1

# Comando para iniciar la aplicación
ENTRYPOINT ["/app/docker-entrypoint.sh"]