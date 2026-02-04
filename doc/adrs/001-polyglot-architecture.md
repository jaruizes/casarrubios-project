# ADR-001: Polyglot Architecture with Multiple Languages and Frameworks

## Status

Accepted

## Context

This project is designed as a portfolio and learning platform to showcase various software development concepts and technologies. The business case (AI-powered recruitment system) provides a realistic scenario for demonstrating different architectural patterns, programming paradigms, and technology stacks.

When deciding how to implement the microservices architecture, we faced the choice between:
1. **Monoglot approach**: Use a single programming language and framework across all services for consistency and maintainability
2. **Polyglot approach**: Use multiple programming languages and frameworks to demonstrate different technologies and their appropriate use cases

The project structure includes multiple contexts (Candidates and Recruitment) with various service types:
- Frontend applications requiring modern UI frameworks
- Backend for Frontend (BFF) services requiring efficient API orchestration
- Business services with different characteristics (CRUD operations, streaming, AI/ML processing)
- Data integration services for CDC and event processing

## Decision

We will adopt a **polyglot architecture** using multiple programming languages and frameworks across the system:

**Frontend & BFF Layer:**
- **Angular 18** for frontend applications (TypeScript)
- **NestJS 11** for Backend for Frontend services (Node.js/TypeScript)

**Backend Services:**
- **Spring Boot 3.4** (Java 21) for traditional business services requiring robust ecosystem and enterprise patterns
- **Quarkus** (Java 21) for services requiring fast startup, low memory footprint, and reactive capabilities
- **Python 3.9+** with **FastAPI** for AI/ML services and rapid prototyping

**Data Integration:**
- **Debezium** for Change Data Capture (CDC)
- **Kafka Streams** (within Quarkus services) for stream processing

Specific technology choices per service:
- **Applications Manager Service**: Spring Boot (mature ecosystem, transaction management)
- **Positions Service**: Spring Boot (consistent with Applications Manager)
- **Positions Manager Service**: Quarkus (demonstrates alternative JVM framework)
- **Applications Service**: Python/FastAPI (rapid development, simple CRUD)
- **Scoring Service**: Python (AI/ML libraries, OpenAI integration)
- **Resume Analyzer Service**: Spring Boot (complex business logic, integration capabilities)
- **Insights Service**: Quarkus with Kafka Streams (stream processing efficiency)
- **Notifications Service**: Quarkus (reactive, event-driven)
- **Applications Updater**: Quarkus (Outbox pattern, transactional messaging)

## Consequences

### Positive Consequences

**Learning and Portfolio Benefits:**
- Demonstrates proficiency across multiple technology stacks
- Shows understanding of appropriate technology selection for different use cases
- Provides practical examples of polyglot persistence and architecture
- Enables exploration of different programming paradigms (OOP, functional, reactive)

**Technical Benefits:**
- Allows choosing the best tool for each specific job (Python for AI, Java for enterprise, Node for BFF)
- Enables comparison of frameworks (Spring Boot vs Quarkus) in similar contexts
- Freedom to experiment with different approaches without being locked into a single stack
- Services can evolve independently with different technology choices

**Architectural Benefits:**
- Reinforces strong service boundaries and loose coupling
- Demonstrates true microservices independence
- Shows capability to integrate heterogeneous systems

### Negative Consequences

**Development Complexity:**
- Requires knowledge and maintenance of multiple programming languages and frameworks
- Higher cognitive load when switching between services
- More difficult to enforce consistent coding standards across the entire project
- Testing strategies vary across technologies

**Operational Complexity:**
- Different build tools and processes (Maven, Gradle, npm, pip)
- Different deployment artifacts (JARs, Docker images)
- Multiple runtime environments and dependencies to manage
- More complex dependency security scanning across ecosystems

**Team and Maintenance:**
- In a production environment, would require team members skilled in multiple technologies
- Higher onboarding complexity for new developers
- Knowledge silos could form around specific technologies
- More challenging to move developers between services

**Infrastructure:**
- Different observability and monitoring approaches across languages
- Multiple dependency management systems to maintain
- Larger total memory footprint across all services
- More complex CI/CD pipelines

### Mitigations

To address the negative consequences while maintaining the learning benefits:

1. **Standardization where possible:**
   - Common patterns for configuration (environment variables)
   - Standardized OpenTelemetry integration for observability
   - Consistent API-first approach with OpenAPI/AsyncAPI
   - Docker containerization for all services

2. **Documentation:**
   - Comprehensive README files per service
   - Technology-specific documentation in `/doc/topics/`
   - Clear architectural diagrams showing technology choices

3. **Testing:**
   - Testcontainers for consistent integration testing approach
   - Contract testing between services regardless of technology

4. **Deployment:**
   - Unified Docker Compose configuration for local development
   - Kubernetes deployment (planned) abstracts away language differences

### Notes

This decision is appropriate for a **portfolio and learning platform** as explicitly stated in the project README. In a production environment focused on a single product, a more conservative approach with 1-2 primary languages might be more appropriate for team efficiency and maintenance.

The polyglot approach here serves the educational purpose while still demonstrating production-ready architectural patterns and practices.
