#!/bin/bash
set -e

echo "Iniciando aplicación..."

# Usar la variable de entorno PORT o el valor por defecto 8000
PORT=${PORT:-9081}
echo "Usando puerto: $PORT"

exec python -m uvicorn src.main:startup --host 0.0.0.0 --port $PORT --factory
