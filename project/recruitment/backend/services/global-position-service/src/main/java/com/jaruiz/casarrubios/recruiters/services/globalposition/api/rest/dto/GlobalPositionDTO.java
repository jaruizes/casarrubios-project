package com.jaruiz.casarrubios.recruiters.services.globalposition.api.rest.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GlobalPositionDTO {
    private long totalPositions;
    private double averageApplications;
    private double averageScore;
}
