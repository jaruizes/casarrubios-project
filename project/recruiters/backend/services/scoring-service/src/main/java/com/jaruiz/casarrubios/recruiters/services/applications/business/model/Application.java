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
    private String name;
    private String email;
    private String phone;
    private String cv;
    private long positionId;
    private long createdAt;

}
