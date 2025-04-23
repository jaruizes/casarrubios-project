# Applications Manager Service

## Overview
The Applications Manager Service is a Spring Boot application that manages job applications for the Casarrubios recruitment platform. It provides an API for candidates to upload their CVs when applying for open positions.

## Features
- Upload CV files for job applications
- Store candidate information
- Associate applications with specific job positions
- Store CV files in MinIO object storage
- Store application metadata in PostgreSQL database

## Technology Stack
- Java 21
- Spring Boot 3.4.0
- Spring Data JPA
- PostgreSQL (for metadata storage)
- MinIO (for CV file storage)
- OpenAPI/Swagger (for API documentation)
- OpenTelemetry (for monitoring and tracing)
- Docker (for containerization)
- Kubernetes/Helm (for deployment)

## API Documentation
The service exposes a REST API with the following endpoint:

- `POST /positions/applications` - Upload a CV for a job application

The API is documented using OpenAPI. When the service is running, you can access the Swagger UI at:
```
http://localhost:8090/swagger-ui.html
```

## Development Setup

### Prerequisites
- Java 21
- Maven
- Docker and Docker Compose
- PostgreSQL
- MinIO

### Building the Application
```bash
# Build with Maven
mvn clean package

# Build Docker image
./build-image.sh
```

### Configuration
The application can be configured using the following properties:

#### Database Configuration
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/candidates
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.properties.hibernate.default_schema=applications
```

#### MinIO Configuration
```properties
minio.url=http://localhost:9000
minio.access.name=minioadmin
minio.access.secret=minioadmin
cv.bucket.name=resumes
```

## Deployment

### Local Development
For local development, you can use Docker Compose:

```bash
# Start the required services (PostgreSQL, MinIO)
docker-compose up -d

# Run the application
mvn spring-boot:run
```

### Kubernetes Deployment
The service can be deployed to Kubernetes using Helm:

```bash
# Deploy using Helm
helm install applications-manager-service platform/k8s/helm/candidates/applications-manager-service
```

#### Helm Configuration
The Helm chart can be configured using the following values:

- `replicaCount`: Number of replicas
- `image.repository`: Docker image repository
- `image.tag`: Docker image tag
- `service.port`: Service port
- `config.*`: Application configuration
- `secrets.*`: Sensitive configuration

## Testing
The application includes unit and integration tests:

```bash
# Run tests
mvn test

# Run tests with coverage
mvn verify
```

## Monitoring
The service is instrumented with OpenTelemetry for monitoring and tracing. Metrics and traces are exported to the configured OpenTelemetry collector.
