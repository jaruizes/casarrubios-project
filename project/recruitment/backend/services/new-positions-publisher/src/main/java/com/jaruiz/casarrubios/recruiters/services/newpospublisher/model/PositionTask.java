package com.jaruiz.casarrubios.recruiters.services.newpospublisher.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PositionTask {
    private long id;
    @JsonProperty("position_id")
    private long positionId;
    private String description;
}
