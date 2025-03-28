package com.jaruiz.casarrubios.notifications.services.business.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationScore {
    private String applicationId;
    private long positionId;
    private BigDecimal score;
}
