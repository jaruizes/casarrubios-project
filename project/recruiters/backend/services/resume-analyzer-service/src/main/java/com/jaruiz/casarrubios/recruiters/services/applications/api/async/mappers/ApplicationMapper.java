package com.jaruiz.casarrubios.recruiters.services.applications.api.async.mappers;

import com.jaruiz.casarrubios.recruiters.services.applications.api.async.dto.NewApplicationReceivedDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.api.async.dto.ResumeAnalysisDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Application;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.ResumeAnalysis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApplicationMapper {
    Application newApplicationReceivedToApplication(NewApplicationReceivedDTO newApplicationReceivedDTO);

    @Mapping(target = "strengths", source = "strengths")
    @Mapping(target = "concerns", source = "concerns")
    ResumeAnalysisDTO cvAnalysisToResumeAnalysisDTO(ResumeAnalysis resumeAnalysis);
}
