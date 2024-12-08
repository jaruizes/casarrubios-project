package com.jaruiz.casarrubios.recruiters.services.posmanager.business.models;

public class Requirement {
    private final String description;

    public Requirement(String description) {
        this.description = description;
    }

    public boolean isValid() {
        return (description != null && !description.isEmpty());
    }

    public String getDescription() {
        return description;
    }
}
