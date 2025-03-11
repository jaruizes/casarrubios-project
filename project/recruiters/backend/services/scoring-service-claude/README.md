# Application Scoring Service

Microservicio que procesa eventos de análisis de aplicaciones, calcula puntuaciones y genera eventos de resultado.

## Descripción

Este microservicio está diseñado para:

1. Consumir eventos de tipo `ApplicationAnalysedEvent` desde Kafka
2. Consultar información adicional en la base de datos PostgreSQL
3. Calcular diferentes puntuaciones para una aplicación basadas en varios criterios
4. Producir eventos de tipo `ApplicationScoredEvent` con los resultados

## Arquitectura

El servicio sigue la arquitectura de Puertos y Adaptadores (Hexagonal):

- **Dominio**: Contiene la lógica de negocio central (modelos y servicios)
- **Aplicación**: Coordina el flujo de la aplicación a través de casos de uso
  - **Puertos**: Interfaces para comunicarse con sistemas externos
  - **Adaptadores**: Implementaciones concretas de los puertos para sistemas específicos
- **Infraestructura**: Proporciona servicios técnicos como configuración, observabilidad, etc.

## Tecnologías

- Python 3.11+
- PostgreSQL (acceso mediante AsyncPG)
- Kafka (usando aiokafka)
- OpenTelemetry para observabilidad

## Instalación y Ejecución

### Instalación con Docker

1. Construir la imagen:

```bash
docker build -t application-scoring-service .
```

2. Ejecutar el contenedor:

```bash
docker run -d \
  --name scoring-service \
  -e DB_HOST=postgres \
  -e KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  --network my-network \
  application-scoring-service
```

### Instalación local (desarrollo)

1. Crear y activar un entorno virtual:

```bash
python -m venv venv
source venv/bin/activate  # En Windows: venv\Scripts\activate
```

2. Instalar dependencias de desarrollo:

```bash
pip install -r requirements-dev.txt
```

3. Ejecutar el servicio:

```bash
python -m src.main
```

## Pruebas

### Ejecutar pruebas unitarias

```bash
pytest tests/unit/
```

### Ejecutar pruebas de integración

Estas pruebas utilizan Testcontainers para levantar automáticamente contenedores de PostgreSQL y Kafka:

```bash
pytest tests/integration/
```

### Ejecutar todas las pruebas

```bash
pytest
```

## Estructura de eventos

### Evento de entrada (ApplicationAnalysedEvent)

```json
{
  "applicationId": "app-123",
  "positionId": 1,
  "candidateDescription": "Descripción del candidato...",
  "requirements": {
    "programming": "python",
    "framework": "fastapi",
    "database": "postgresql"
  },
  "experiences": {
    "previous_job": "Experiencia previa...",
    "education": "Formación...",
    "projects": "Proyectos..."
  }
}
```

### Evento de salida (ApplicationScoredEvent)

```json
{
  "applicationId": "app-123",
  "score": 85.5,
  "descScore": 90.0,
  "requirementScore": 80.0,
  "tasksScore": 75.0,
  "timeSpent": 0.153
}
```

## Observabilidad

El servicio está instrumentado con OpenTelemetry para proporcionar trazas y métricas. Asegúrese de configurar la variable de entorno `OTLP_EXPORTER_ENDPOINT` para apuntar a su colector OTLP.

Los principales puntos de observabilidad incluyen:
- Procesamiento de eventos
- Consultas a la base de datos
- Cálculo de puntuaciones
- Publicación de eventos de resultado Requisitos previos

- Docker y Docker Compose
- Python 3.11 o superior
- PostgreSQL 14 o superior
- Kafka

### Configuración

El servicio se configura mediante variables de entorno:

```
# Base de datos
DB_HOST=localhost
DB_PORT=5432
DB_USER=postgres
DB_PASSWORD=postgres
DB_NAME=postgres

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_CONSUMER_GROUP=application-scoring-service
KAFKA_INPUT_TOPIC=application-analysed-events
KAFKA_OUTPUT_TOPIC=application-scored-events

# Telemetría
TELEMETRY_ENABLED=true
OTLP_EXPORTER_ENDPOINT=http://localhost:4317
```

###