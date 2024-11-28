package com.jaruiz.casarrubios.candidates.services.positionsservice.api.rest.dto;

import java.io.Serializable;

public class ConditionDTO implements Serializable {

    private final long id;
    private final String description;

    public ConditionDTO(long id, String description) {
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
