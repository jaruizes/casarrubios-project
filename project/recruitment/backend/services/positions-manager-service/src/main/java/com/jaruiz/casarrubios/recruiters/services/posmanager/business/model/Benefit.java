package com.jaruiz.casarrubios.recruiters.services.posmanager.business.model;

public class Benefit {

    private final String description;

    public Benefit(String description) {
        this.description = description;
    }

    public boolean isValid() {
        return (description != null && !description.isEmpty());
    }

    public String getDescription() {
        return description;
    }
}
