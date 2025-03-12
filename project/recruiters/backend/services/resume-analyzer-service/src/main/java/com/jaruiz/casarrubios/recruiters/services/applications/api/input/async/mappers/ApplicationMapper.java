package com.jaruiz.casarrubios.recruiters.services.applications.api.input.async.mappers;

import com.jaruiz.casarrubios.recruiters.services.applications.api.input.async.dto.NewApplicationReceivedDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Application;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Component;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Component("applicationMapper")
public interface ApplicationMapper {
    Application newApplicationReceivedToApplication(NewApplicationReceivedDTO newApplicationReceivedDTO);
}
