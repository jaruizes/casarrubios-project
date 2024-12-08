package com.jaruiz.casarrubios.recruiters.services.posmanager.business.models;

public class Condition {

    private final String description;

    public Condition(String description) {
        this.description = description;
    }

    public boolean isValid() {
        return (description != null && !description.isEmpty());
    }

    public String getDescription() {
        return description;
    }
}
