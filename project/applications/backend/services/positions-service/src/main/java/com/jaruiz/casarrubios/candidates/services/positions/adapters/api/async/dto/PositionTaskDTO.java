package com.jaruiz.casarrubios.candidates.services.positions.adapters.api.async.dto;

public class PositionTaskDTO {
    private long id;
    private long positionId;
    private String description;

    public PositionTaskDTO() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
