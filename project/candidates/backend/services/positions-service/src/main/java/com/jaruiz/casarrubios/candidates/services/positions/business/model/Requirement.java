package com.jaruiz.casarrubios.candidates.services.positions.business.model;

public class Requirement {
    private long id;
    private final String key;
    private final String value;
    private final String description;
    private final Boolean isMandatory;

    public Requirement(long id, String key, String value, String description, Boolean isMandatory) {
        this.id = id;
        this.key = key;
        this.description = description;
        this.value = value;
        this.isMandatory = isMandatory;
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

    public Boolean getMandatory() {
        return isMandatory;
    }

    public long getId() {
        return id;
    }
}
