package com.jaruiz.casarrubios.recruiters.services.applications.api.async.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationAnalysedEventDTO implements Serializable {
    private UUID applicationId;
    private ResumeAnalysisDTO analysis;
}
