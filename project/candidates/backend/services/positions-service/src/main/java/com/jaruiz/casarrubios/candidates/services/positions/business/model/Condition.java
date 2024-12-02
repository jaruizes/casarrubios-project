package com.jaruiz.casarrubios.candidates.services.positions.business.model;

public class Condition {

    private final long id;
    private final String description;

    public Condition(long id, String description) {
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
