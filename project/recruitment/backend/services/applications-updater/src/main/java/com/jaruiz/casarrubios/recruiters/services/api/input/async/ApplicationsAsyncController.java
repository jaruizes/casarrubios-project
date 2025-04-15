package com.jaruiz.casarrubios.recruiters.services.api.input.async;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.jaruiz.casarrubios.recruiters.services.api.input.async.dto.ApplicationCDCDTO;
import com.jaruiz.casarrubios.recruiters.services.business.ApplicationsUpdaterService;
import com.jaruiz.casarrubios.recruiters.services.business.model.Application;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ApplicationsAsyncController {
    private static final Logger logger = Logger.getLogger(ApplicationsAsyncController.class);
    private final ApplicationsUpdaterService applicationsUpdaterService;

    public ApplicationsAsyncController(ApplicationsUpdaterService applicationsUpdaterService) {
        this.applicationsUpdaterService = applicationsUpdaterService;
    }

    @Incoming("cdc-applications")
    public void applicationCDCEventHandler(ApplicationCDCDTO applicationCDCDTO) {
        logger.info("Received application: " + applicationCDCDTO);

        final List<String> validationErrors = validateApplication(applicationCDCDTO);
        if (!validationErrors.isEmpty()) {
            logger.error("Application rejected due to validation errors: " + validationErrors);
            return;
        }

        this.applicationsUpdaterService.processApplication(mapToApplication(applicationCDCDTO));
    }

    private Application mapToApplication(ApplicationCDCDTO applicationCDCDTO) {
        return new Application(applicationCDCDTO.getId(),
            applicationCDCDTO.getName(),
            applicationCDCDTO.getEmail(),
            applicationCDCDTO.getPhone(),
            applicationCDCDTO.getCv(),
            applicationCDCDTO.getPositionId(),
            applicationCDCDTO.getCreatedAt());
    }

    private List<String> validateApplication(ApplicationCDCDTO applicationCDCDTO) {
        final var id = applicationCDCDTO.getId();

        List<Function<ApplicationCDCDTO, String>> validators = List.of(
            dto -> dto.getId() == null ? "Invalid id" : null,
            dto -> isEmpty(dto.getName()) ? "[Application ID: " + id + "] Invalid name" : null,
            dto -> isEmpty(dto.getEmail()) ? "[Application ID: " + id + "] Invalid email" : null,
            dto -> isEmpty(dto.getPhone()) ? "[Application ID: " + id + "] Invalid phone" : null,
            dto -> isEmpty(dto.getCv()) ? "[Application ID: " + id + "] Invalid cv" : null,
            dto -> dto.getPositionId() == null ? "[Application ID: " + id + "] Invalid positionId" : null,
            dto -> dto.getCreatedAt() == null ? "[Application ID: " + id + "] Invalid createdAt" : null
        );

        return validators.stream()
                         .map(validator -> validator.apply(applicationCDCDTO))
                         .filter(Objects::nonNull)
                         .collect(Collectors.toList());
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
