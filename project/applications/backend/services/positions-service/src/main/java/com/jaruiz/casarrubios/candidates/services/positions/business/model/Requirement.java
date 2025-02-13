package com.jaruiz.casarrubios.candidates.services.positions.business.model;

public class Requirement {
    private final long id;
    private final String description;

    public Requirement(long id, String description) {
        this.id = id;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
