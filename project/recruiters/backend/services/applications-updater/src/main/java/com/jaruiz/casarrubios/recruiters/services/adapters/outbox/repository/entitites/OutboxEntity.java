package com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository.entitites;

import java.time.Instant;
import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository.dto.NewApplicationReceivedDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "outbox")
public class OutboxEntity {
    @Id
    private UUID id;

    @Column(name = "aggregatetype", nullable = false)
    private String aggregateType;

    @Column(name = "aggregateid", nullable = false)
    private String aggregateId;

    @Column(name = "type", nullable = false)
    private String type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    private NewApplicationReceivedDTO payload;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    public OutboxEntity(String aggregateType, String aggregateId, String type, NewApplicationReceivedDTO payload) {
        this.id = UUID.randomUUID();
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.type = type;
        this.payload = payload;
        this.timestamp = Instant.now();
    }

    public OutboxEntity() { }

    public UUID getId() {
        return id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getType() {
        return type;
    }

    public NewApplicationReceivedDTO getPayload() {
        return payload;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
