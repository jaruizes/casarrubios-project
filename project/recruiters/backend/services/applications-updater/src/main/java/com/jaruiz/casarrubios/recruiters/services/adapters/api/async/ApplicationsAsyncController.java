package com.jaruiz.casarrubios.recruiters.services.adapters.api.async;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.jaruiz.casarrubios.recruiters.services.adapters.api.async.dto.ApplicationDTO;
import com.jaruiz.casarrubios.recruiters.services.business.ApplicationsUpdaterService;
import com.jaruiz.casarrubios.recruiters.services.business.model.Application;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ApplicationsAsyncController {
    private static final Logger logger = Logger.getLogger(ApplicationsAsyncController.class);
    private final ApplicationsUpdaterService applicationsUpdaterService;

    @Inject
    @Channel("applications-processed") MutinyEmitter<ApplicationDTO> applicationProcessedEmitter;

    public ApplicationsAsyncController(ApplicationsUpdaterService applicationsUpdaterService) {
        this.applicationsUpdaterService = applicationsUpdaterService;
    }

    @Incoming("cdc-applications")
    @Transactional
    public void processApplication(ApplicationDTO applicationDTO) {
        logger.info("Received application: " + applicationDTO);

        final List<String> validationErrors = validateApplication(applicationDTO);
        if (!validationErrors.isEmpty()) {
            logger.error("Application rejected due to validation errors: " + validationErrors);
            return;
        }

        this.applicationsUpdaterService.processApplication(mapToApplication(applicationDTO));
        logger.error("Application <" + applicationDTO.getId() + "> processed");

        sendApplicationProcessed(applicationDTO);
    }

    private void sendApplicationProcessed(ApplicationDTO applicationDTO) {
        this.applicationProcessedEmitter.sendAndAwait(applicationDTO);
        logger.error("Application <" + applicationDTO.getId() + "> send to applications-processed");
    }

    private Application mapToApplication(ApplicationDTO applicationDTO) {
        return new Application(applicationDTO.getId(),
            applicationDTO.getName(),
            applicationDTO.getEmail(),
            applicationDTO.getPhone(),
            applicationDTO.getCv(),
            applicationDTO.getPositionId(),
            applicationDTO.getCreatedAt());
    }

    private List<String> validateApplication(ApplicationDTO applicationDTO) {
        final var id = applicationDTO.getId();

        List<Function<ApplicationDTO, String>> validators = List.of(
            dto -> dto.getId() == null ? "Invalid id" : null,
            dto -> isEmpty(dto.getName()) ? "[Application ID: " + id + "] Invalid name" : null,
            dto -> isEmpty(dto.getEmail()) ? "[Application ID: " + id + "] Invalid email" : null,
            dto -> isEmpty(dto.getPhone()) ? "[Application ID: " + id + "] Invalid phone" : null,
            dto -> isEmpty(dto.getCv()) ? "[Application ID: " + id + "] Invalid cv" : null,
            dto -> dto.getPositionId() == null ? "[Application ID: " + id + "] Invalid positionId" : null,
            dto -> dto.getCreatedAt() == null ? "[Application ID: " + id + "] Invalid createdAt" : null
        );

        return validators.stream()
                         .map(validator -> validator.apply(applicationDTO))
                         .filter(Objects::nonNull)
                         .collect(Collectors.toList());
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }


//    private boolean isApplicationValid(ApplicationDTO applicationDTO) {
//        final var id = applicationDTO.getId();
//        if (id == null) {
//            logger.info("Recevied application with invalid id");
//            return false;
//        }
//
//        if (applicationDTO.getName() == null || applicationDTO.getName().isEmpty()) {
//            logger.info("[Application ID: " + id + "] Recevied application with invalid name");
//            return false;
//        }
//
//        if (applicationDTO.getEmail() == null || applicationDTO.getEmail().isEmpty()) {
//            logger.info("[Application ID: " + id + "] Recevied application with invalid email");
//            return false;
//        }
//
//        if (applicationDTO.getPhone() == null || applicationDTO.getPhone().isEmpty()) {
//            logger.info("[Application ID: " + id + "] Recevied application with invalid phone");
//            return false;
//        }
//
//        if (applicationDTO.getCv() == null || applicationDTO.getCv().isEmpty()) {
//            logger.info("[Application ID: " + id + "] Recevied application with invalid cv");
//            return false;
//        }
//
//        if (applicationDTO.getPositionId() == null) {
//            logger.info("[Application ID: " + id + "] Recevied application with invalid positionId");
//            return false;
//        }
//
//        if (applicationDTO.getCreatedAt() == null) {
//            logger.info("[Application ID: " + id + "] Recevied application with invalid createdAt");
//            return false;
//        }
//
//        return true;
//    }

}
