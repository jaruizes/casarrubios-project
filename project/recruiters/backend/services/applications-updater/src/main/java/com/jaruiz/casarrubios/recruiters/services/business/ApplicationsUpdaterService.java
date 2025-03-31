package com.jaruiz.casarrubios.recruiters.services.business;

import com.jaruiz.casarrubios.recruiters.services.business.model.Application;
import com.jaruiz.casarrubios.recruiters.services.business.ports.NewApplicationReceivedEventPublisherPort;
import com.jaruiz.casarrubios.recruiters.services.business.ports.PersistenceService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ApplicationsUpdaterService {

    private static final Logger logger = Logger.getLogger(ApplicationsUpdaterService.class);

    private final PersistenceService persistenceService;
    private final NewApplicationReceivedEventPublisherPort newApplicationReceivedEventPublisher;

    public ApplicationsUpdaterService(PersistenceService persistenceService, NewApplicationReceivedEventPublisherPort newApplicationReceivedEventPublisher) {
        this.persistenceService = persistenceService;
        this.newApplicationReceivedEventPublisher = newApplicationReceivedEventPublisher;
    }

    @Transactional
    public void processApplication(Application application) {
        logger.info("Processing application with Id: " + application.getId());

        this.persistenceService.saveApplication(application);
        this.newApplicationReceivedEventPublisher.publishNewApplicationReceivedEvent(application);

        logger.info("Application with Id: " + application.getId() + " processed");
    }
}
