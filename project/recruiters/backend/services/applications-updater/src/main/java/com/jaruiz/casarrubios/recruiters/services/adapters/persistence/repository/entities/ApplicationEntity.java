package com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.entities;

import java.sql.Timestamp;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "candidate_applications")
public class ApplicationEntity {

    @Id
    private UUID id;

    @Column(name = "candidate_id")
    private UUID candidateId;

    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public UUID getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(UUID candidateId) {
        this.candidateId = candidateId;
    }

    public ApplicationEntity() { }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }
}
