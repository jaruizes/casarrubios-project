package com.jaruiz.casarrubios.recruiters.services.applications.business.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Application {

    private UUID id;
    private UUID candidateId;
    private long positionId;
    private long createdAt;

}
