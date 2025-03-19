package com.jaruiz.casarrubios.recruiters.services.newpospublisher.model;

import lombok.Data;

@Data
public class PositionRequirement {
    private long id;
    private long positionId;
    private String key;
    private String value;
    private String description;
    private boolean mandatory;
}
