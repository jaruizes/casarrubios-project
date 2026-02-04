# ADR-005: Domain-Driven Design with Bounded Contexts

## Status

Accepted

## Context

The AI-powered recruitment system involves multiple domains:
- **Candidates Domain**: Job seekers browsing positions and submitting applications
- **Recruitment Domain**: Recruiters managing positions, reviewing applications, and viewing AI-powered insights

When designing the system, we needed to decide how to organize the code and data:
1. Should we build a single monolithic application with shared database?
2. Should we split into microservices, and if so, how?
3. How should we handle entities that appear in both domains (Position, Application)?
4. How do we ensure each service/domain can evolve independently?

**Key Challenges:**
- **Shared concepts**: Position and Application entities exist in both domains but with different perspectives
- **Data ownership**: Who owns the authoritative data for each entity?
- **Coupling**: How do we avoid tight coupling between domains?
- **Autonomy**: Each domain should evolve independently with its own team, technology, and release cycle

**Options Considered:**

**Option 1: Monolithic Shared Database**
- Single application and database for all functionality
- **Challenges**: Tight coupling, difficult to scale teams, shared schema evolution

**Option 2: Microservices with Shared Database**
- Multiple services sharing same database
- **Challenges**: Still coupled at data layer, can't evolve schemas independently

**Option 3: Domain-Driven Design with Bounded Contexts**
- Separate contexts with independent databases
- Clear ownership boundaries
- Context integration via well-defined contracts

## Decision

We will adopt **Domain-Driven Design (DDD)** principles with **Bounded Contexts** to organize the system into two independent contexts:

1. **Candidates Context** (Application/Consumption)
2. **Recruitment Context** (Management/Analytics)

Each bounded context will have:
- Independent team ownership (conceptually)
- Own database(s) with full schema autonomy  
- Own services and deployment artifacts
- Own business logic and rules
- Clear API boundaries

**Context Definitions:**

### Candidates Context

**Purpose**: Enable candidates to discover positions and apply for jobs

**Core Entities:**
- **Position** (Projection/Read-Only): Job opportunities to apply to
  - Data source: Replicated from Recruitment context
  - Operations: Read-only (list, search, view details)
  - Local storage: Optimized for candidate browsing experience
  
- **Application** (Owned Entity): Candidate job applications
  - Data source: Created in this context (master data)
  - Operations: Create application, upload resume
  - Propagated to: Recruitment context for processing

**Services:**
- Candidates App (Angular frontend)
- Candidates BFF (NestJS)
- Applications Manager Service (Spring Boot) - Owns application creation
- Positions Service (Spring Boot) - Serves position projections

**Database:**
- Applications Database: Master for applications
- Positions Database: Replica/projection from Recruitment

**Context Boundary:**
- Published Events: New applications created
- Consumed Events: Position changes from Recruitment context
- No direct database access from outside this context

### Recruitment Context

**Purpose**: Enable recruiters to manage positions, review applications with AI insights

**Core Entities:**
- **Position** (Owned Entity): Job openings managed by recruiters
  - Data source: Created in this context (master data)
  - Operations: CRUD operations, manage requirements/responsibilities/benefits
  - Propagated to: Candidates context for candidate browsing

- **Application** (Projection): Applications received from candidates
  - Data source: Replicated from Candidates context
  - Operations: Read, enrich with scoring and analysis
  - Enhanced with: Scoring, Resume Analysis, Candidate profile

- **Candidate** (Derived Entity): Extracted from applications
  - Data source: Derived from application data
  - Purpose: Independent candidate analysis and future recommendations

- **Scoring** (Owned Entity): AI-calculated candidate-position match
  - Embedding-based similarity score
  - LLM-generated explanation

- **Candidate Analysis** (Owned Entity): AI-extracted insights from resume

**Services:**
- Recruitment App (Angular frontend)
- Recruitment BFF (NestJS)
- Positions Manager Service (Quarkus) - Owns position management
- Applications Service (Python/FastAPI) - Serves enriched applications
- Resume Analyzer Service (Spring Boot) - Analyzes candidate resumes
- Scoring Service (Python) - Calculates candidate-position matching
- Insights Service (Quarkus/Kafka Streams) - Aggregates analytics
- Notifications Service (Quarkus) - Real-time recruiter notifications
- Applications Updater (Quarkus) - Processes incoming applications

**Databases:**
- Positions Database: Master for positions
- Applications Database: Replica + enriched with scoring/analysis
- Qdrant: Vector database for embeddings

**Context Boundary:**
- Published Events: Position changes, scoring results, notifications
- Consumed Events: New applications from Candidates context
- No direct database access from outside this context

### Context Integration

**Master Data & Projections Pattern:**
- Each context owns the master copy of certain entities
- Other contexts maintain projections (read-only copies)
- Synchronization via Change Data Capture (CDC) and events

**Data Flow:**
```
Position Created in Recruitment → CDC → Event → Candidates Positions DB (projection)
Application Created in Candidates → CDC → Event → Recruitment Applications DB (projection + enrichment)
```

**Integration Mechanisms:**
- **Change Data Capture (Debezium)**: Captures database changes
- **Event Streaming (Kafka)**: Asynchronous event-driven integration
- **API Contracts (OpenAPI)**: Synchronous APIs within context boundaries
- **No Shared Database**: Strict database isolation between contexts

## Consequences

### Positive Consequences

**Autonomy and Independence:**
- Each context can evolve its data model independently
- Technology choices can differ per context (polyglot persistence)
- Services can be deployed independently
- Teams can work in parallel without coordination overhead
- Release cycles can be independent

**Clarity and Understanding:**
- Clear ownership of data and business rules
- Ubiquitous language within each bounded context
- Easier to reason about scope of changes
- Domain experts can focus on their specific context

**Scalability:**
- Can scale contexts independently based on load
- Candidates context (read-heavy) scales differently than Recruitment (write-heavy analytics)
- Database partitioning aligned with business boundaries
- Easier to identify performance bottlenecks

**Resilience:**
- Failure in one context doesn't directly impact the other
- Can deploy updates to one context without affecting the other
- Each context has its own database, reducing blast radius

**Encapsulation:**
- Business logic encapsulated within context boundaries
- Changes within context don't leak out
- Protects context invariants and business rules
- Clean API contracts between contexts

### Negative Consequences

**Complexity:**
- Data duplication across contexts (positions, applications)
- Need to manage data synchronization and eventual consistency
- More complex system architecture with multiple databases
- Distributed data management challenges

**Consistency:**
- **Eventual consistency**: Changes in one context take time to propagate
- Need to handle scenarios where data is temporarily out of sync
- UI must handle in-progress state (e.g., "Position being published...")
- No cross-context ACID transactions

**Operations:**
- More databases to manage, backup, and monitor
- Need to coordinate schema migrations carefully
- More complex deployment pipeline
- Higher infrastructure costs (multiple databases)

**Queries:**
- No joins across contexts (can't join positions from recruitment with applications from candidates)
- Need to denormalize data or make multiple service calls
- Aggregated reporting across contexts more complex
- CQRS and materialized views needed for complex queries

**Development:**
- Developers need to understand bounded context concepts
- More services to develop and maintain
- Testing cross-context flows requires more setup
- Need clear documentation of context boundaries

### Design Principles Applied

**1. Single Responsibility Principle (SRP)**
- Each context has a clear, single purpose
- Services within context have focused responsibilities

**2. Loose Coupling**
- Contexts integrate via events and APIs, not shared databases
- No compile-time dependencies between contexts
- Interface segregation at context boundaries

**3. High Cohesion**
- Related entities and business logic grouped in same context
- Ubiquitous language consistent within context

**4. Separation of Concerns**
- Read models separated from write models (CQRS-like)
- Different scalability characteristics per context

### Data Consistency Strategy

**Master/Source of Truth:**
- **Positions**: Recruitment context is master
- **Applications**: Candidates context is master

**Projections/Replicas:**
- Never modified in consuming context
- Refreshed via CDC events
- May have different schema optimized for consuming context's needs

**Conflict Resolution:**
- Timestamp-based (last-write-wins for updates)
- Event sourcing provides complete history
- Manual reconciliation for critical mismatches

**User Experience:**
- Loading states for async operations
- Eventual consistency communicated to users
- Optimistic UI updates where appropriate
- Polling or WebSocket for real-time updates

### Context Mapping

Following DDD Context Mapping patterns:

**Candidates ← Recruitment (Positions):**
- **Pattern**: Customer/Supplier with Conformist
- Recruitment (upstream) defines position schema
- Candidates (downstream) conforms to position structure
- Integration: One-way event flow

**Recruitment ← Candidates (Applications):**
- **Pattern**: Customer/Supplier
- Candidates (upstream) publishes application events
- Recruitment (downstream) enriches with additional data
- Integration: One-way event flow + local enrichment

**Anti-Corruption Layer:**
- Applications Updater acts as ACL for Recruitment context
- Transforms external (Candidates) application format to internal model
- Protects Recruitment context from external changes

### Future Considerations

**Additional Contexts:**
- **Analytics Context**: Dedicated context for reporting and BI
- **Notification Context**: Separate context for all notification types
- **Search Context**: Specialized context for full-text search and recommendations

**Shared Kernel:**
- Currently minimal shared code
- Could extract common libraries (e.g., OpenTelemetry integration)
- Keep shared kernel minimal to preserve autonomy

**Published Language:**
- Consider standardizing event schemas across contexts
- AsyncAPI documentation for event contracts
- Schema Registry for versioning and compatibility

## References

- Eric Evans - *Domain-Driven Design: Tackling Complexity in the Heart of Software*
- Vaughn Vernon - *Implementing Domain-Driven Design*
- Martin Fowler - [BoundedContext](https://martinfowler.com/bliki/BoundedContext.html)
- Chris Richardson - [Microservices Patterns: Bounded Context](https://microservices.io/patterns/decomposition/decompose-by-business-capability.html)
- Project: `/doc/core/architecture/architecture.md` (Business Architecture section)
