# Architecture Decision Records (ADRs)

This directory contains Architecture Decision Records (ADRs) documenting the key architectural decisions made in this project.

An ADR is a document that captures an important architectural decision made along with its context and consequences. For more information about ADRs, see [Architecture Decision Records](../topics/architecture-decision-record.md).

## Index of ADRs

### Core Architecture Decisions

| ADR | Title | Status | Date |
|-----|-------|--------|------|
| [ADR-001](001-polyglot-architecture.md) | Polyglot Architecture with Multiple Languages and Frameworks | Accepted | 2024 |
| [ADR-002](002-event-driven-architecture.md) | Event-Driven Architecture with Apache Kafka | Accepted | 2024 |
| [ADR-005](005-domain-driven-design-bounded-contexts.md) | Domain-Driven Design with Bounded Contexts | Accepted | 2024 |

### Data Integration & Consistency

| ADR | Title | Status | Date |
|-----|-------|--------|------|
| [ADR-003](003-change-data-capture-debezium.md) | Change Data Capture with Debezium | Accepted | 2024 |
| [ADR-004](004-transactional-outbox-pattern.md) | Transactional Outbox Pattern | Accepted | 2024 |

## Decision Categories

### Technology Choices
- **Polyglot Architecture** (ADR-001): Why we use multiple programming languages and frameworks
- **Event-Driven Architecture** (ADR-002): Why we chose Apache Kafka for async communication

### Architectural Patterns
- **Domain-Driven Design** (ADR-005): How we organize code into bounded contexts
- **Event-Driven Architecture** (ADR-002): Event-based integration between services
- **Change Data Capture** (ADR-003): Using Debezium for data synchronization
- **Transactional Outbox** (ADR-004): Ensuring reliable event publishing

### Data Management
- **Bounded Contexts** (ADR-005): Independent databases per context
- **CDC** (ADR-003): Log-based change capture from PostgreSQL
- **Outbox Pattern** (ADR-004): Atomic database writes and event publishing

## Quick Reference

### When to Create an ADR?

Create an ADR when making decisions that:
- Affect the overall architecture or system structure
- Have significant impact on development, deployment, or operations
- Introduce new technologies or frameworks
- Define patterns that other developers should follow
- Involve trade-offs that need to be documented
- Are difficult or costly to reverse

### ADR Template

Use the [template.md](template.md) file as a starting point for new ADRs.

### ADR Naming Convention

ADRs follow the naming pattern: `NNN-title-with-dashes.md`
- `NNN`: Three-digit sequential number (001, 002, 003, ...)
- `title-with-dashes`: Kebab-case title describing the decision

### ADR Statuses

- **Proposed**: Decision is proposed but not yet accepted
- **Accepted**: Decision is approved and should be followed
- **Deprecated**: Decision is no longer current but kept for historical reference
- **Superseded**: Decision has been replaced by a newer ADR
- **Rejected**: Proposed decision was not accepted

## Related Documentation

- [Architecture Overview](../core/architecture/architecture.md) - Detailed architecture documentation
- [Technical Requirements](../core/architecture/tech_reqs.md) - Technology stack requirements
- [ADR Methodology](../topics/architecture-decision-record.md) - Explanation of ADR approach

## Future ADRs to Consider

Based on the project roadmap and TODOs, consider documenting:

- **Schema Registry and Avro**: Event schema management strategy
- **AsyncAPI Specification**: Event-driven API documentation
- **API Gateway Strategy**: Kong or alternative for API management
- **Kubernetes Deployment**: Container orchestration decisions
- **Infrastructure as Code**: Terraform/IaC approach for cloud deployment
- **Authentication & Authorization**: Keycloak integration design
- **Observability Stack**: Metrics, logging, and tracing strategy
- **Internal Developer Portal**: Backstage setup and governance
- **GitOps Workflow**: CI/CD and deployment automation
- **Testing Strategy**: Pyramid, contract testing, and quality gates
