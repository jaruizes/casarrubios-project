# Technical Requirements

This section outlines the technical requirements for working with this project. The project uses a variety of technologies across different components.

### Development Tools

- **Docker**: Required for local development and deployment
- **Docker Compose**: Required for orchestrating local development environment
- **Git**: Required for version control
- **Maven**: Required for building Java applications
- **Poetry**: Required for Python dependency management
- **Node.js**: Required for frontend and BFF development
- **npm/yarn**: Required for JavaScript/TypeScript dependency management

### Backend Technologies

#### Java
- **Java 21**: Required for Spring Boot applications
- **Spring Boot 3.4.0**: Used for Java backend services
- **Quarkus**: Used for some recruitment context services
- **Hibernate/JPA**: Used for database access in Java applications
- **Testcontainers**: Used for integration testing

#### Python
- **Python 3.9+**: Required for Python applications
- **FastAPI 0.100.0+**: Used for Python REST APIs
- **SQLAlchemy 2.0.0+**: Used for database access in Python applications
- **Uvicorn 0.22.0+**: ASGI server for Python applications

#### Node.js
- **NestJS 11.0.0+**: Used for Backend for Frontend (BFF) services

### Frontend Technologies

- **Angular 18.1.0+**: Used for frontend applications
- **TypeScript 5.5.0+**: Used for type-safe JavaScript development
- **Bootstrap 5.3.0+**: Used for UI components and styling
- **RxJS 7.8.0+**: Used for reactive programming

### Infrastructure

- **PostgreSQL**: Used for relational databases
- **Kafka**: Used for event streaming and messaging
- **Debezium**: Used for Change Data Capture (CDC)
- **Minio**: Used for object storage (S3-compatible)
- **OpenTelemetry**: Used for distributed tracing and observability
- **Jaeger**: Used for visualizing distributed traces

### Cloud Requirements (AWS)

- **Terraform 1.0.0+**: Used for Infrastructure as Code
- **AWS CLI**: Recommended for AWS interactions
- **AWS Services**:
    - EKS (Elastic Kubernetes Service)
    - RDS (Relational Database Service)
    - S3 (Simple Storage Service)
    - CloudFront (Content Delivery Network)
    - VPC (Virtual Private Cloud)

### AI Services

- **OpenAI API**: Required for AI-powered scoring and analysis functionality
