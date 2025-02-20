package com.jaruiz.casarrubios.candidates.services.applications.adapters.api.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.stream.Collectors;

import com.jaruiz.casarrubios.candidates.services.applications.business.exceptions.ApplicationIncompleteException;
import com.jaruiz.casarrubios.candidates.services.applications.business.exceptions.ApplicationsGeneralException;
import com.jaruiz.casarrubios.candidates.services.applications.adapters.api.rest.dto.ApplicationErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
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
import org.springframework.web.context.request.ServletWebRequest;
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
        logger.error(ex.getMessage());

        // Loggea los headers y el contenido de la request
        logger.error("Headers: " + headers.toString());

        // Extrae y loggea los parámetros de la request
        if (request instanceof ServletWebRequest) {
            ServletWebRequest servletRequest = (ServletWebRequest) request;
            HttpServletRequest httpServletRequest = servletRequest.getRequest();

            // Loggear los parámetros en caso de `multipart/form-data`
            if (httpServletRequest.getContentType() != null && httpServletRequest.getContentType().contains("multipart/form-data")) {
                try {
                    Collection<Part> parts = httpServletRequest.getParts();
                    for (Part part : parts) {
                        logger.error("Received part: " + part.getName() + ", Size: " + part.getSize());
                    }
                } catch (Exception e) {
                    logger.error("Error extracting multipart parts", e);
                }
            }

            // Loggea los parámetros del body
            try {
                String requestBody = new BufferedReader(new InputStreamReader(httpServletRequest.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
                logger.error("Request Body: " + requestBody);
            } catch (IOException e) {
                logger.error("Error reading request body", e);
            }
        }

        // Retornar la respuesta de error
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
