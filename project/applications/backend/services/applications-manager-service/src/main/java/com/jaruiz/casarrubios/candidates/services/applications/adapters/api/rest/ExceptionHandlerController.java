package com.jaruiz.casarrubios.candidates.services.applications.adapters.api.rest;

import com.jaruiz.casarrubios.candidates.services.applications.business.exceptions.ApplicationIncompleteException;
import com.jaruiz.casarrubios.candidates.services.applications.business.exceptions.ApplicationsGeneralException;
import com.jaruiz.casarrubios.candidates.services.applications.adapters.api.rest.dto.ApplicationErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {
    public static final String APPLICACION_INCOMPLETE = "INCOMPLETE";
    public static final String PDF_ERROR = "PDFERROR";
    public static final String MISSING_PARAMS = "MISINGPARAMS";

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        logger.error("Exception captured uploading CV: missing params");
        final ApplicationErrorDTO applicationErrorDTO = new ApplicationErrorDTO();
        applicationErrorDTO.setErrorCode(MISSING_PARAMS);
        return new ResponseEntity<>(applicationErrorDTO, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ApplicationsGeneralException.class)
    protected ResponseEntity<ApplicationErrorDTO> handleApplicationsGeneralException(ApplicationsGeneralException ex) {
        logger.error("Exception captured uploading CV [applicationId = {}, error = {}]", ex.getApplicationId(), ex.getCode());

        final ApplicationErrorDTO applicationErrorDTO = new ApplicationErrorDTO();
        applicationErrorDTO.setApplicationId(ex.getApplicationId().toString());
        applicationErrorDTO.setErrorCode(ex.getCode());

        return new ResponseEntity<>(applicationErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApplicationIncompleteException.class)
    protected ResponseEntity<ApplicationErrorDTO> handleApplicationIncompleteException(ApplicationIncompleteException ex) {
        logger.error("Exception captured uploading CV: application is incomplete");

        final ApplicationErrorDTO applicationErrorDTO = new ApplicationErrorDTO();
        applicationErrorDTO.setErrorCode(APPLICACION_INCOMPLETE);

        return new ResponseEntity<>(applicationErrorDTO, HttpStatus.BAD_REQUEST);
    }

}
