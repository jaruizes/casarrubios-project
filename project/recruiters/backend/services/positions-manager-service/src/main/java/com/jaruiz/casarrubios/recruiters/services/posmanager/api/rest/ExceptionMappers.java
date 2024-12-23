package com.jaruiz.casarrubios.recruiters.services.posmanager.api.rest;

import com.jaruiz.casarrubios.recruiters.services.posmanager.api.rest.dto.ApplicationErrorDTO;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionNotFoundException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class ExceptionMappers {

    @ServerExceptionMapper
    public RestResponse<ApplicationErrorDTO> handlePositionNotFoundException(PositionNotFoundException positionNotFoundException) {
        final ApplicationErrorDTO error = new ApplicationErrorDTO();
        error.setApplicationId("posmanager");
        error.setErrorCode(positionNotFoundException.getCode());

        return RestResponse.status(Response.Status.NOT_FOUND,error);
    }
}
