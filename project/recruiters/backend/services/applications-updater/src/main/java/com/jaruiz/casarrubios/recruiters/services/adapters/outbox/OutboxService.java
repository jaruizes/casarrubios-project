package com.jaruiz.casarrubios.recruiters.services.adapters.outbox;

import com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository.OutboxRepository;
import com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository.dto.NewApplicationReceivedDTO;
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

    private NewApplicationReceivedDTO buildOutboxPayload(Application application) {
        final NewApplicationReceivedDTO newApplicationReceivedDTO = new NewApplicationReceivedDTO();
        newApplicationReceivedDTO.setId(application.getId());
        newApplicationReceivedDTO.setName(application.getName());
        newApplicationReceivedDTO.setEmail(application.getEmail());
        newApplicationReceivedDTO.setPhone(application.getPhone());
        newApplicationReceivedDTO.setCv(application.getCv());
        newApplicationReceivedDTO.setPositionId(application.getPositionId());
        newApplicationReceivedDTO.setCreatedAt(application.getCreatedAt());


        return newApplicationReceivedDTO;
    }
}
