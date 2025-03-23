package com.jaruiz.casarrubios.candidates.services.positions.adapters.api.async.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PositionRequirementDTO {
    private long id;
    @JsonProperty("position_id")
    private long positionId;
    private String key;
    private String value;
    private String description;
    private boolean mandatory;

    public PositionRequirementDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPositionId() {
        return positionId;
    }

    public void setPositionId(long positionId) {
        this.positionId = positionId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}
