package com.jaruiz.casarrubios.recruiters.services.posmanager.business.model;

public class Requirement {
    private final String key;
    private final String description;

    public Requirement(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public boolean isValid() {
        return ((key != null && !key.isEmpty()) && (description != null && !description.isEmpty()));
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return key;
    }
}
