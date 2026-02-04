# ADR-003: Change Data Capture with Debezium

## Status

Accepted

## Context

In our event-driven architecture, we need to synchronize data between the Candidates and Recruitment contexts:
- **Positions** are created in Recruitment context and need to be available in Candidates context (read-only)
- **Applications** are created in Candidates context and need to be processed in Recruitment context

This data synchronization requirement raised several design questions:
1. How do we detect when data changes in one context?
2. How do we reliably propagate these changes to other contexts?
3. How do we ensure transactional consistency between database writes and event publishing?
4. How do we avoid coupling the source service to downstream consumers?

**Options Considered:**

**Option 1: Application-Level Event Publishing**
- Application code explicitly publishes events after database operations
- **Challenges:**
  - Risk of inconsistency if event publish fails after database commit
  - Requires distributed transaction coordination or complex error handling
  - Application must be aware of all downstream consumers
  - Code intrusion in business logic

**Option 2: Polling-Based Change Detection**
- Periodically query database for changes (e.g., updated_at timestamp)
- **Challenges:**
  - Inefficient for high-frequency changes
  - Delay in detecting changes (polling interval)
  - Complex tracking of what's already been processed
  - Missing deletes if records are hard-deleted

**Option 3: Database Triggers**
- Use database triggers to publish to message queue
- **Challenges:**
  - Complex trigger logic couples database to messaging system
  - Limited error handling in triggers
  - Difficult to test and maintain
  - Performance impact on write operations

**Option 4: Change Data Capture (CDC) with Debezium**
- Dedicated CDC platform reads database transaction logs
- **Benefits:**
  - No application code changes needed
  - Guaranteed consistency (reads committed transactions)
  - Low latency event capture
  - No performance impact on application database writes
  - Supports deletes and schema changes

## Decision

We will use **Debezium** for Change Data Capture (CDC) to propagate data changes between contexts.

**Implementation:**

**Positions Synchronization (Recruitment → Candidates):**
1. **Debezium Connector** reads PostgreSQL WAL (Write-Ahead Log) from Recruitment Positions Database
2. Publishes changes to topics:
   - `cdc.recruiters.positions.positions`
   - `cdc.recruiters.positions.requirements`
   - `cdc.recruiters.positions.responsibilities`
   - `cdc.recruiters.positions.benefits`
3. **Positions Publisher** (Quarkus service with Kafka Streams) consumes CDC events
4. Transforms and publishes to business-level topic `recruiters.positions`
5. **Positions Service** (Candidates context) consumes and updates local database

**Applications Synchronization (Candidates → Recruitment):**
1. **Debezium Connector** reads PostgreSQL WAL from Candidates Applications Database
2. Publishes changes to topic: `cdc.candidates.applications.applications`
3. **Applications Updater** (Recruitment context, Quarkus) consumes CDC events
4. Updates local Applications Database and publishes business event via Outbox pattern

**Debezium Configuration:**
- **Connector Type**: PostgreSQL
- **Plugin**: pgoutput (native logical replication)
- **Publication**: Captures INSERT, UPDATE, DELETE operations
- **Snapshot Mode**: Initial snapshot followed by streaming changes
- **Topic Naming**: `<server-name>.<database>.<schema>.<table>`
- **Message Format**: JSON (Debezium envelope with before/after values)

**Physical Architecture:**
- Debezium runs as Kafka Connect connectors
- Deployed in Docker Compose environment
- Configuration managed via Kafka Connect REST API
- Monitors PostgreSQL replication slots

## Consequences

### Positive Consequences

**Reliability and Consistency:**
- **Guaranteed delivery**: CDC reads committed transactions from database log
- **Exactly-once semantics**: Each database change captured exactly once
- **No dual-write problem**: Single source of truth is the database transaction log
- **Ordering preserved**: Events published in same order as database commits
- **Captures all operations**: INSERT, UPDATE, DELETE automatically detected

**Decoupling:**
- **Zero application code changes**: Source services unaware of CDC
- **No coupling to consumers**: Database changes don't know about downstream services
- **Schema evolution**: Debezium handles database schema changes
- **Independent scaling**: CDC infrastructure scales separately from applications

**Performance:**
- **Low latency**: Near real-time change detection (milliseconds)
- **No impact on writes**: Reads from transaction log asynchronously
- **Efficient**: Only processes committed transactions
- **Scalable**: Can handle high-throughput databases

**Operational Benefits:**
- **Monitoring**: Debezium provides metrics for lag, throughput, errors
- **Reprocessing**: Can replay from earlier points in transaction log
- **Disaster recovery**: Initial snapshot capability for rebuilding downstream databases
- **Audit trail**: Complete change history captured

### Negative Consequences

**Complexity:**
- **Additional infrastructure**: Requires Kafka Connect cluster
- **PostgreSQL configuration**: Needs logical replication enabled (WAL level = logical)
- **Learning curve**: Team needs to understand CDC concepts and Debezium
- **Debugging**: Tracking issues through CDC pipeline can be complex

**Operational Overhead:**
- **PostgreSQL replication slots**: Must monitor and manage to prevent WAL growth
- **Connector management**: Need to configure and monitor Debezium connectors
- **Schema changes**: Database migrations require coordination with CDC
- **Resource usage**: CDC consumes database and network resources

**Dependencies:**
- **Database requirements**: PostgreSQL must support logical replication (PostgreSQL 10+)
- **Kafka dependency**: CDC tied to Kafka availability
- **Version coupling**: Debezium version compatibility with PostgreSQL
- **Network connectivity**: Debezium must have network access to database

**Message Format:**
- **Debezium envelope**: Messages include metadata (before/after values, transaction info)
- **Transformation needed**: Often need Kafka Streams or service to transform to business events
- **Large payloads**: Full row data can be large for wide tables
- **Schema management**: Need to handle evolving message schemas

**Database Constraints:**
- **Primary keys required**: Tables need primary keys for proper CDC
- **Replication slot management**: Unused slots can fill up disk with WAL files
- **Not all operations captured**: Some DDL operations may need special handling

### Mitigations

**Operational Concerns:**
1. **Monitoring**:
   - Monitor replication slot lag via PostgreSQL metrics
   - Debezium connector metrics in Kafka Connect
   - Alerts for connector failures or high lag

2. **Resource Management**:
   - Regular cleanup of unused replication slots
   - WAL archive retention policies
   - Connector resource limits in Kafka Connect

3. **Schema Changes**:
   - Document schema change procedures
   - Test migrations in development environment first
   - Use online schema change tools when possible

**Transformation Layer:**
1. **Business Event Transformation**:
   - Positions Publisher (Kafka Streams) transforms CDC to business events
   - Filters unnecessary fields
   - Enriches with business context
   - Publishes clean business events for consumers

2. **Outbox Pattern Integration**:
   - Applications Updater uses CDC for reliable event publishing
   - Combines CDC with Outbox table for transactional guarantees

**Testing:**
- Testcontainers for integration tests with Debezium
- Test database schema migrations with CDC active
- Verify CDC behavior with different operation types

### Alternative Considered: Outbox Pattern Everywhere

**Outbox Pattern**: Application writes to database and outbox table in same transaction, then CDC reads outbox table.

**Why we use both:**
- **Debezium CDC alone** for simple data replication (Positions → Candidates)
- **Outbox Pattern + Debezium** for complex workflows requiring business events (Applications processing)

The combination provides:
- Simplicity where appropriate (direct CDC)
- Transactional guarantees where needed (Outbox)
- Flexibility to choose the right pattern per use case

### Future Considerations

- **Schema Registry**: Add schema versioning and validation for CDC messages
- **Avro format**: Consider Avro instead of JSON for better schema evolution
- **Filtering**: Add Debezium SMTs (Single Message Transforms) to filter at source
- **Metrics**: Enhance monitoring with custom metrics for business-level insights

## References

- [Debezium Documentation](https://debezium.io/documentation/)
- [PostgreSQL Logical Replication](https://www.postgresql.org/docs/current/logical-replication.html)
- Martin Kleppmann - [Change Data Capture](https://www.confluent.io/blog/using-logs-to-build-a-solid-data-infrastructure-or-why-dual-writes-are-a-bad-idea/)
- Project: `/doc/topics/` (CDC-related documentation planned)
