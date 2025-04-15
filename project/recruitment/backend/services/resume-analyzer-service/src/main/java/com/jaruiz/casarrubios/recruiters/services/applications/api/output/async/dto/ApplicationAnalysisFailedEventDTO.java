package com.jaruiz.casarrubios.recruiters.services.applications.api.output.async.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationAnalysisFailedEventDTO {
    private String applicationId;
    private String code;
    private String message;
    private final long timestamp = System.currentTimeMillis();
}
