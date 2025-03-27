# Imagen base con Python
FROM python:3.11-slim

# Establecer directorio de trabajo
WORKDIR /app

# Instalar Poetry
RUN apt-get update && apt-get install -y curl \
  && curl -sSL https://install.python-poetry.org | python3 - \
  && ln -s /root/.local/bin/poetry /usr/local/bin/poetry \
  && apt-get purge -y curl \
  && rm -rf /var/lib/apt/lists/*

# Copiar archivos de dependencias
COPY pyproject.toml poetry.lock* /app/

# Configurar Poetry para instalar en el entorno global del contenedor
ENV POETRY_VIRTUALENVS_CREATE=false \
    PYTHONUNBUFFERED=1 \
    PYTHONPATH=/app

# Instalar dependencias
RUN poetry install --no-root --only main

# Copiar el código fuente
COPY src/ /app/src/

# Comando por defecto
CMD ["python", "-m", "src.main"]