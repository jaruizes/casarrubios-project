package com.jaruiz.casarrubios.recruiters.services.business.model;

import java.util.UUID;

public class Candidate {

    private UUID id;
    private final String name;
    private final String email;
    private final String phone;
    private final String cv;
    private final long createdAt;

    public Candidate(String name, String email, String phone, String cv, long createdAt) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cv = cv;
        this.createdAt = createdAt;
    }

    public void setId(UUID id) {
        this.id = id;
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
}
