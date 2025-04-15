package com.jaruiz.casarrubios.recruiters.services.adapters.outbox;

import com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository.OutboxRepository;
import com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository.dto.NewApplicationReceivedEventDTO;
import com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository.entitites.OutboxEntity;
import com.jaruiz.casarrubios.recruiters.services.business.model.Application;
import com.jaruiz.casarrubios.recruiters.services.business.ports.NewApplicationReceivedEventPublisherPort;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OutboxService implements NewApplicationReceivedEventPublisherPort {
    private static final Logger logger = Logger.getLogger(OutboxService.class);

    private final OutboxRepository outboxRepository;

    public OutboxService(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Override
    public void publishNewApplicationReceivedEvent(Application application) {
        final OutboxEntity outboxEntity = new OutboxEntity(
            NEW_APPLICATION_RECEIVED_EVENT_TYPE,
            application.getId().toString(),
            NEW_APPLICATION_RECEIVED_EVENT,
            buildOutboxPayload(application)
        );

        this.outboxRepository.persist(outboxEntity);
        logger.info("Published new application received event. Application ID: " + application.getId());
    }

    private NewApplicationReceivedEventDTO buildOutboxPayload(Application application) {
        final NewApplicationReceivedEventDTO newApplicationReceivedEventDTO = new NewApplicationReceivedEventDTO();
        newApplicationReceivedEventDTO.setId(application.getId());
        newApplicationReceivedEventDTO.setCandidateId(application.getCandidate().getId());
        newApplicationReceivedEventDTO.setPositionId(application.getPositionId());
        newApplicationReceivedEventDTO.setCreatedAt(application.getCreatedAt());


        return newApplicationReceivedEventDTO;
    }
}
