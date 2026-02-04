# ADR-002: Event-Driven Architecture with Apache Kafka

## Status

Accepted

## Context

The AI-powered recruitment system involves two main bounded contexts (Candidates and Recruitment) that need to:
1. Share data between contexts while maintaining autonomy
2. Trigger asynchronous workflows (resume analysis, scoring, notifications)
3. Enable independent evolution of each context
4. Handle eventual consistency across distributed data

When designing inter-service communication, we needed to choose between:
1. **Synchronous REST/HTTP**: Direct request-response communication between services
2. **Event-Driven Architecture (EDA)**: Asynchronous communication via events
3. **Hybrid approach**: Mix of synchronous and asynchronous patterns

Key requirements driving this decision:
- Candidate applications trigger a multi-step AI analysis pipeline (extract resume → analyze content → calculate scoring → notify recruiters)
- Position data created in Recruitment context must be available in Candidates context (and vice versa for applications)
- Services should be decoupled and able to evolve independently
- The system should be resilient to temporary service failures
- Support for future capabilities like analytics, audit logging, and event replay

## Decision

We will adopt an **Event-Driven Architecture (EDA)** using **Apache Kafka** as the central event streaming platform for asynchronous communication between services.

**Architecture Pattern:**
- Services publish domain events when significant state changes occur
- Services subscribe to events they need to react to
- No direct service-to-service synchronous calls for cross-context operations
- Frontend applications use synchronous REST APIs to their respective BFF services

**Kafka Topic Strategy:**
- **Change Data Capture (CDC) Topics**: Debezium publishes database changes
  - `cdc.candidates.applications`
  - `cdc.recruiters.positions`
  - `cdc.recruiters.outbox`
  
- **Business Event Topics**: Application-level events
  - `candidates.applications.received` (New application received)
  - `recruiters.candidates.analyzed` (Resume analysis completed)
  - `recruiters.scoring.calculated` (Scoring calculated)
  - `recruiters.notifications` (High-score notifications)

**Event Flow Example (Application Submission):**
1. Candidate submits application → Applications Manager Service creates record in database
2. Debezium CDC detects database change → publishes to `cdc.candidates.applications`
3. Applications Updater (Recruitment context) consumes CDC event → creates local application record
4. Applications Updater publishes `candidates.applications.received` event (via Outbox pattern)
5. Resume Analyzer consumes event → analyzes resume → publishes `candidates.analyzed` event
6. Scoring Service consumes event → calculates score → publishes `scoring.calculated` event
7. Notifications Service consumes scoring event → publishes notification if score > threshold
8. Insights Service consumes multiple events → maintains aggregated insights

**Services NOT using events:**
- BFF to Business Service communication (synchronous REST within same context)
- Frontend to BFF communication (synchronous REST)
- Read operations within same context

## Consequences

### Positive Consequences

**Decoupling and Autonomy:**
- Services are loosely coupled and can evolve independently
- No compile-time dependencies between services
- Services can be deployed, scaled, and updated independently
- New consumers can be added without modifying producers

**Scalability and Performance:**
- Asynchronous processing enables better resource utilization
- Services can process events at their own pace
- Kafka provides horizontal scalability for high-throughput scenarios
- Long-running AI analysis doesn't block user requests

**Resilience and Reliability:**
- Kafka provides durable message storage with configurable retention
- Failed consumers can retry without losing events
- Services can be temporarily unavailable without data loss
- Event replay capability for recovery or reprocessing

**Observability and Auditability:**
- All significant state changes are captured as events
- Event log provides complete audit trail
- Easy to understand system behavior by examining event flows
- Supports debugging and troubleshooting complex workflows

**Extensibility:**
- Easy to add new event consumers (e.g., analytics, reporting, ML training)
- Supports future features without modifying existing services
- Enables event sourcing patterns if needed
- Foundation for CQRS (Command Query Responsibility Segregation)

### Negative Consequences

**Complexity:**
- Eventual consistency requires careful design and user experience consideration
- Distributed tracing needed to understand request flows (using OpenTelemetry/Jaeger)
- Debugging is more complex than synchronous request-response
- Need to handle duplicate events and ensure idempotency

**Operational Overhead:**
- Kafka cluster adds infrastructure to manage and monitor
- Need to manage topic configuration, retention policies, and partitioning
- Requires monitoring Kafka lag and consumer group health
- Schema evolution and compatibility management needed

**Development Challenges:**
- Developers need to understand asynchronous programming models
- Testing is more complex (need to verify event publishing and consumption)
- Local development requires running Kafka (addressed via Docker Compose)
- Error handling patterns different from synchronous code

**Data Consistency:**
- Eventual consistency model requires business logic to handle intermediate states
- No distributed transactions across services (requires Saga pattern)
- Need to handle out-of-order events
- Compensating actions needed for business workflow failures

**Performance Considerations:**
- Higher latency for operations requiring multiple event hops
- Serialization/deserialization overhead
- Network overhead for event transmission
- Need to carefully design event granularity (too fine vs too coarse)

### Mitigations

To address the negative consequences:

1. **Eventual Consistency:**
   - UI shows loading states and progress indicators
   - Polling or WebSocket for real-time updates (BFF pushes notifications to frontend)
   - Clear messaging to users about asynchronous processing

2. **Observability:**
   - OpenTelemetry integration in all services for distributed tracing
   - Jaeger for visualizing request flows across services
   - Kafka UI for monitoring topics and consumer groups
   - Structured logging with correlation IDs

3. **Development Experience:**
   - Testcontainers for integration testing with real Kafka
   - Docker Compose for local development environment
   - Clear documentation of event schemas and flows
   - Example code patterns for event publishing and consumption

4. **Reliability Patterns:**
   - Idempotent event handlers (using event IDs)
   - Outbox pattern for transactional event publishing
   - Dead letter queues for failed events
   - Retry policies with exponential backoff

5. **Schema Management:**
   - OpenAPI for REST APIs
   - Plan to add Schema Registry and Avro for event schemas (see TODO.md)
   - AsyncAPI documentation for event contracts (planned)

### Alternative Considered: Synchronous REST

**Why not chosen:**
- Would create tight coupling between contexts
- Harder to scale individual services independently
- Cascading failures when services are unavailable
- Difficult to add new capabilities without modifying existing services
- Doesn't support audit trail and event replay naturally

**Where still used:**
- Within bounded context boundaries (BFF → Business Service)
- Read operations that don't cross context boundaries
- User-facing APIs requiring immediate responses

### Future Considerations

- **Schema Registry**: Add Confluent Schema Registry or Apicurio for event schema management
- **AsyncAPI**: Document event-driven APIs with AsyncAPI specification
- **Event Catalog**: Implement event catalog for discovery and documentation
- **CQRS**: Consider CQRS pattern for complex read models
- **Event Sourcing**: Evaluate event sourcing for audit-heavy domains

## References

- Martin Fowler - [What do you mean by "Event-Driven"?](https://martinfowler.com/articles/201701-event-driven.html)
- Chris Richardson - [Pattern: Event-driven architecture](https://microservices.io/patterns/data/event-driven-architecture.html)
- TODO.md mentions: Schema Registry, Avro, AsyncAPI, Event Catalog
