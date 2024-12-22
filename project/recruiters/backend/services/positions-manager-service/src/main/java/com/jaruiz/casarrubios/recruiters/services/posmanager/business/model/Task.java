package com.jaruiz.casarrubios.recruiters.services.posmanager.business.model;

public class Task {

    private final String description;

    public Task(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isValid() {
        return (description != null && !description.isEmpty());
    }
}
