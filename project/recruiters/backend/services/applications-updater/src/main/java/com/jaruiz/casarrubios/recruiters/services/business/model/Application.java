package com.jaruiz.casarrubios.recruiters.services.business.model;

import java.util.UUID;

public class Application {

    private final UUID id;
    private final Candidate candidate;
    private final long positionId;
    private final long createdAt;

    public Application(UUID id, String name, String email, String phone, String cv, long positionId, long createdAt) {
        this.id = id;
        this.positionId = positionId;
        this.createdAt = createdAt;
        this.candidate = new Candidate(name, email, phone, cv, createdAt);
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getPositionId() {
        return positionId;
    }

    public Candidate getCandidate() {
        return candidate;
    }
}
