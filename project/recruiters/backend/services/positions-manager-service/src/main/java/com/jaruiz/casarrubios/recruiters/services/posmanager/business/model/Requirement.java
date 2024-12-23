package com.jaruiz.casarrubios.recruiters.services.posmanager.business.model;

public class Requirement {
    private final String key;
    private final String value;
    private final String description;
    private Boolean isMandatory;

    public Requirement(String key, String value, String description, Boolean isMandatory) {
        this.key = key;
        this.description = description;
        this.value = value;
        this.isMandatory = isMandatory;
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

    public String getValue() {
        return value;
    }

    public Boolean isMandatory() {
        return isMandatory;
    }
}
