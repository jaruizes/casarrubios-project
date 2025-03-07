package com.jaruiz.casarrubios.recruiters.services.applications.business.model;

import java.util.UUID;

public class Application {

    private final UUID id;
    private final String name;
    private final String email;
    private final String phone;
    private final String cv;
    private final long positionId;
    private final long createdAt;

    public Application(UUID id, String name, String email, String phone, String cv, long positionId, long createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cv = cv;
        this.positionId = positionId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCv() {
        return cv;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getPositionId() {
        return positionId;
    }
}
