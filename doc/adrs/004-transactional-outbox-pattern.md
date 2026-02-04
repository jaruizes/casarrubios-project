# ADR-004: Transactional Outbox Pattern

## Status

Accepted

## Context

When a service needs to update its database AND publish an event to Kafka, we face the **dual-write problem**:
- Updating the database succeeds but event publishing fails → data inconsistency
- Event publishing succeeds but database transaction rolls back → incorrect events published
- Without distributed transactions, we cannot guarantee atomicity across database and message broker

This problem is critical for the **Applications Updater** service in the Recruitment context:
1. Receives application data via CDC from Candidates context
2. Must write to local Applications Database (application, candidate records)
3. Must publish "New Application Received" event to trigger scoring pipeline
4. Any failure must maintain consistency between database and events

**Requirements:**
- Atomic updates: Database write and event publishing must succeed or fail together
- Guaranteed delivery: Events must be published if database commit succeeds
- No message loss: Failed publishes must be retried
- Exactly-once semantics: Each business event published exactly once
- Performance: Solution must handle production-level throughput

**Options Considered:**

**Option 1: Direct Kafka Publish in Transaction**
```java
@Transactional
public void processApplication(Application app) {
    repository.save(app);
    kafkaTemplate.send("topic", event); // PROBLEM: Not part of DB transaction
}
```
**Issue**: Kafka send is not part of database transaction, can fail independently

**Option 2: Kafka Transactions (XA/2PC)**
- Use distributed transactions across PostgreSQL and Kafka
- **Challenges**:
  - Complex configuration and management
  - Performance overhead of two-phase commit
  - Kafka transactions not mature/widely adopted
  - Locks and potential deadlocks
  - Not all brokers support XA

**Option 3: Polling Outbox Table**
- Application writes to outbox table in same transaction
- Separate poller reads outbox and publishes to Kafka
- **Challenges**:
  - Polling interval introduces latency
  - Complex state management (processed/unprocessed)
  - Scalability issues with high throughput
  - Inefficient resource usage

**Option 4: Transactional Outbox with CDC**
- Write to outbox table in same transaction as business data
- Debezium CDC reads outbox table and publishes to Kafka
- **Benefits**:
  - True atomicity (single database transaction)
  - Low latency (CDC is near real-time)
  - Guaranteed delivery (Debezium reliability)
  - No polling overhead
  - Scalable

## Decision

We will implement the **Transactional Outbox Pattern** using Debezium CDC for the Applications Updater service.

**Implementation:**

**Database Schema:**
```sql
CREATE TABLE outbox (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

**Application Code (Applications Updater - Quarkus):**
```java
@Transactional
public void processNewApplication(ApplicationCDC cdcEvent) {
    // 1. Write business data
    Application application = createApplication(cdcEvent);
    applicationRepository.persist(application);
    
    Candidate candidate = createCandidate(cdcEvent);
    candidateRepository.persist(candidate);
    
    // 2. Write to outbox table (same transaction)
    OutboxEvent outboxEvent = OutboxEvent.builder()
        .aggregateType("Application")
        .aggregateId(application.getId())
        .eventType("candidates.applications.received")
        .payload(createEventPayload(application, candidate))
        .build();
    outboxRepository.persist(outboxEvent);
    
    // 3. Commit transaction - both business data and outbox record committed atomically
}
```

**CDC Configuration:**
- Debezium connector monitors `outbox` table
- Publishes to topic: `cdc.recruiters.outbox`
- Outbox Publisher service transforms CDC events to business events
- Publishes to: `candidates.applications.received` topic

**Event Flow:**
1. Applications Updater receives CDC event from `cdc.candidates.applications`
2. Single transaction: Insert Application + Candidate + Outbox record
3. Transaction commits (or rolls back atomically)
4. Debezium detects outbox table insert
5. Publishes to `cdc.recruiters.outbox` topic
6. Outbox Publisher transforms and routes to business topic
7. Resume Analyzer Service consumes `candidates.applications.received`

**Outbox Table Cleanup:**
- Outbox records deleted after successful publishing (via separate cleanup process)
- Retention policy: Keep records for 24 hours for debugging/replay

## Consequences

### Positive Consequences

**Reliability:**
- **Atomic writes**: Database and event publishing succeed or fail together
- **Guaranteed delivery**: If transaction commits, event will be published
- **Exactly-once semantics**: Each business operation produces exactly one event
- **No message loss**: Debezium retries on failures
- **Idempotency**: Can safely retry without duplicate business effects

**Performance:**
- **Low latency**: CDC detects changes in milliseconds
- **No polling overhead**: Event-driven via database transaction log
- **Efficient**: Single database transaction for all writes
- **Scalable**: Debezium can handle high-throughput scenarios

**Simplicity:**
- **Standard transaction**: Uses familiar database transactions
- **No distributed transactions**: Avoids complexity of XA/2PC
- **Clear semantics**: Easy to understand and implement
- **Testable**: Can test with standard database transaction tests

**Debugging:**
- **Audit trail**: Outbox table provides record of all events
- **Replayability**: Can reprocess outbox records if needed
- **Observability**: Can query outbox table to see pending events

### Negative Consequences

**Complexity:**
- **Additional table**: Requires outbox table schema
- **CDC dependency**: Relies on Debezium for event publishing
- **Transformation needed**: Outbox CDC events must be transformed to business events
- **Cleanup logic**: Need process to remove processed outbox records

**Storage:**
- **Increased database writes**: Every business operation writes to outbox table
- **Table growth**: Outbox table can grow large if not cleaned up
- **Transaction log size**: More WAL activity due to outbox writes

**Latency:**
- **Slight delay**: Small additional latency compared to direct Kafka publish
- **Multi-hop**: Event goes through outbox → CDC → transformation → business topic
- **Still near real-time**: Typically milliseconds, acceptable for async workflows

**Operations:**
- **Monitoring**: Need to monitor outbox table size and CDC lag
- **Cleanup**: Requires cleanup job to prevent unbounded growth
- **Schema evolution**: Changes to outbox schema require coordination

### Implementation Details

**Quarkus Configuration:**
```properties
# Transaction Management
quarkus.transaction-manager.default-transaction-timeout=30

# Outbox Configuration
mp.messaging.outgoing.outbox.connector=smallrye-kafka
mp.messaging.outgoing.outbox.topic=cdc.recruiters.outbox
```

**Outbox Event Structure:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "aggregate_type": "Application",
  "aggregate_id": "app-123",
  "event_type": "candidates.applications.received",
  "payload": {
    "application_id": "app-123",
    "candidate_id": "candidate-456",
    "position_id": "pos-789",
    "resume_file": "resume-123.pdf",
    "submitted_at": "2024-01-15T10:30:00Z"
  },
  "created_at": "2024-01-15T10:30:00.123Z"
}
```

**Debezium CDC Transformation:**
- CDC captures full outbox row
- Transformation extracts `payload` and `event_type`
- Routes to appropriate business topic
- Adds metadata (event ID, timestamp)

**Cleanup Strategy:**
- Async job runs every hour
- Deletes outbox records older than 24 hours
- Only deletes records successfully published (via CDC offset tracking)
- Keeps failed records for manual investigation

### Mitigations

**Performance:**
- Index on `created_at` for efficient cleanup queries
- Partition outbox table if volume is very high
- Batch cleanup operations

**Monitoring:**
- Alert if outbox table size exceeds threshold
- Monitor CDC lag specifically for outbox connector
- Track outbox record age (time between insert and publish)

**Error Handling:**
- Failed outbox publishing retried by Debezium
- Dead letter queue for persistently failing events
- Manual intervention process for stuck events

**Testing:**
- Integration tests with Testcontainers (PostgreSQL + Kafka + Debezium)
- Verify atomicity: Rollback scenarios
- Test event ordering and idempotency

### Alternative Pattern: Saga Pattern

The Outbox Pattern complements the **Saga Pattern** for distributed transactions:
- **Outbox**: Ensures atomic write + event publish within a service
- **Saga**: Coordinates multi-service transactions via events
- Together: Enable reliable distributed workflows

Example in this project:
- Applications Updater uses Outbox to reliably publish "application received"
- Saga coordinates: Analyze Resume → Calculate Score → Send Notification
- Each step publishes events via similar patterns

### Future Considerations

- **Quarkus Outbox Extension**: Consider using `quarkus-debezium-outbox` for automatic outbox management
- **Event Sourcing**: Outbox pattern is foundation for event sourcing if needed
- **Schema Registry**: Validate outbox event schemas before publishing
- **Metrics**: Add custom metrics for outbox processing times

## References

- Chris Richardson - [Pattern: Transactional Outbox](https://microservices.io/patterns/data/transactional-outbox.html)
- Debezium - [Outbox Event Router](https://debezium.io/documentation/reference/transformations/outbox-event-router.html)
- Gunnar Morling - [Reliable Microservices Data Exchange With the Outbox Pattern](https://debezium.io/blog/2019/02/19/reliable-microservices-data-exchange-with-the-outbox-pattern/)
- Project: Applications Updater service in `/project/recruitment/backend/applications-updater/`
