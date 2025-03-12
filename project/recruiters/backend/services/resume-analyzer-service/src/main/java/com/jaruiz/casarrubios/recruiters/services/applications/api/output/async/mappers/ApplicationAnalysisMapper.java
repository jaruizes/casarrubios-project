package com.jaruiz.casarrubios.recruiters.services.applications.api.output.async.mappers;

import com.jaruiz.casarrubios.recruiters.services.applications.api.output.async.dto.ResumeAnalysisDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.ResumeAnalysis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Component;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Component("applicationAnalysisMapper")
public interface ApplicationAnalysisMapper {
    @Mapping(target = "strengths", source = "strengths")
    @Mapping(target = "concerns", source = "concerns")
    ResumeAnalysisDTO cvAnalysisToResumeAnalysisDTO(ResumeAnalysis resumeAnalysis);
}
