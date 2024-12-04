package com.jaruiz.casarrubios.recruiters.services.adapters.api.async;

import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.adapters.api.async.dto.ApplicationDTO;
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
    public void processApplication(ApplicationDTO applicationDTO) {
        logger.info("Received application: " + applicationDTO);

        this.applicationsUpdaterService.processApplication(mapToApplication(applicationDTO));

        logger.info("Application processed: " + applicationDTO);
    }

    private Application mapToApplication(ApplicationDTO applicationDTO) {
        return new Application(UUID.fromString(applicationDTO.getId()), applicationDTO.getName(), applicationDTO.getSurname(), applicationDTO.getEmail(), applicationDTO.getPhone(), applicationDTO.getCv(), applicationDTO.getCreated_at());
    }

}
