package com.jaruiz.casarrubios.recruiters.services.applications.business.model;

public class Benefit {

    private final long id;
    private final String description;

    public Benefit(long id, String description) {
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
