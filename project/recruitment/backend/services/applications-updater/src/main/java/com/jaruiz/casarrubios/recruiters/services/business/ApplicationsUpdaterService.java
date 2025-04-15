package com.jaruiz.casarrubios.recruiters.services.business;

import java.time.Instant;

import com.jaruiz.casarrubios.recruiters.services.adapters.outbox.events.NewApplicationReceivedEvent;
import com.jaruiz.casarrubios.recruiters.services.business.model.Application;
import com.jaruiz.casarrubios.recruiters.services.business.ports.PersistenceService;
import io.debezium.outbox.quarkus.ExportedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ApplicationsUpdaterService {

    private static final Logger logger = Logger.getLogger(ApplicationsUpdaterService.class);

    private final PersistenceService persistenceService;
//    private final NewApplicationReceivedEventPublisherPort newApplicationReceivedEventPublisher;

    @Inject Event<ExportedEvent<?, ?>> eventsPublisher;

    public ApplicationsUpdaterService(PersistenceService persistenceService, Event<ExportedEvent<?, ?>> eventsPublisher) {
        this.persistenceService = persistenceService;
//        this.newApplicationReceivedEventPublisher = newApplicationReceivedEventPublisher;
        this.eventsPublisher = eventsPublisher;
    }

    @Transactional
    public void processApplication(Application application) {
        logger.info("Processing application with Id: " + application.getId());

        this.persistenceService.saveApplication(application);
        this.eventsPublisher.fire(new NewApplicationReceivedEvent(application.getId(), Instant.now(),application));
//        this.newApplicationReceivedEventPublisher.publishNewApplicationReceivedEvent(application);

        logger.info("Application with Id: " + application.getId() + " processed");
    }
}
