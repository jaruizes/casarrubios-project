package com.jaruiz.casarrubios.recruiters.services.adapters.outbox.events;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jaruiz.casarrubios.recruiters.services.business.model.Application;
import io.debezium.outbox.quarkus.ExportedEvent;

public class NewApplicationReceivedEvent implements ExportedEvent<String, JsonNode> {
    private static final String EVENT_TYPE =  "NewApplicationReceived";
    private static final String TYPE = "Application";
    private static ObjectMapper mapper = new ObjectMapper();

    private final UUID applicationId;
    private final JsonNode jsonNode;
    private final Instant timestamp;

    public NewApplicationReceivedEvent(UUID applicationId, Instant timestamp, Application application) {
        this.applicationId = applicationId;
        this.jsonNode = convertToJson(application);
        this.timestamp = timestamp;
    }

    @Override public String getAggregateId() {
        return this.applicationId.toString();
    }

    @Override public String getAggregateType() {
        return TYPE;
    }

    @Override public String getType() {
        return EVENT_TYPE;
    }

    @Override public Instant getTimestamp() {
        return this.timestamp;
    }

    @Override public JsonNode getPayload() {
        return jsonNode;
    }

    private JsonNode convertToJson(Application application) {
        return mapper.createObjectNode()
                      .put("id", String.valueOf(application.getId()))
                      .put("candidateId", String.valueOf(application.getCandidate().getId()))
                      .put("positionId", application.getPositionId())
                      .put("createdAt", application.getCreatedAt());
    }
}
