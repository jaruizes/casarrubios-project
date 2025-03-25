package com.jaruiz.casarrubios.recruiters.services.globalposition.api.rest.dto;

import lombok.Data;

@Data
public class GlobalPositionDTO {
    private long totalPositions;
    private double averageApplications;
    private double averageScore;
}
