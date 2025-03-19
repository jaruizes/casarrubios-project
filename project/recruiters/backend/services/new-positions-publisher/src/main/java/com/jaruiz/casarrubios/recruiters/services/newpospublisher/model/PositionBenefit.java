package com.jaruiz.casarrubios.recruiters.services.newpospublisher.model;

import lombok.Data;

@Data
public class PositionBenefit {
    private long id;
    private long positionId;
    private String description;
}
