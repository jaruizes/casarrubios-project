package com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class NewApplicationReceivedEventDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private UUID id;
    private UUID candidateId;
    private Long positionId;
    private Long createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(UUID candidateId) {
        this.candidateId = candidateId;
    }
}
