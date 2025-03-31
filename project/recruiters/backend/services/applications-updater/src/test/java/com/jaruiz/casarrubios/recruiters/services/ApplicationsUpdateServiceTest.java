package com.jaruiz.casarrubios.recruiters.services;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository.OutboxRepository;
import com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository.dto.NewApplicationReceivedDTO;
import com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository.entitites.OutboxEntity;
import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.ApplicationsRepository;
import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.entities.ApplicationEntity;
import com.jaruiz.casarrubios.recruiters.services.util.containers.KafkaTestResource;
import com.jaruiz.casarrubios.recruiters.services.util.kafka.CDCEventsProducer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import static com.jaruiz.casarrubios.recruiters.services.business.ports.NewApplicationReceivedEventPublisherPort.NEW_APPLICATION_RECEIVED_EVENT;
import static com.jaruiz.casarrubios.recruiters.services.business.ports.NewApplicationReceivedEventPublisherPort.NEW_APPLICATION_RECEIVED_EVENT_TYPE;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(KafkaTestResource.class)
class ApplicationsUpdateServiceTest {

    @Inject CDCEventsProducer CDCEventsProducer;
    @Inject ApplicationsRepository applicationsRepository;
    @Inject OutboxRepository outboxRepository;

    @Test
    void whenANewApplicationIsSubmitted_thenTheApplicationIsCapturedAndStoredIntoDatabase() {
        final UUID applicationId = CDCEventsProducer.publishChangeEvent();
        Awaitility.await()
                  .atMost(120, TimeUnit.SECONDS)
                  .until(() -> isMessageProcessed(applicationId));
    }

    @Test
    void whenAWrongApplicationIsSubmitted_thenTheApplicationIsNotProcessed() {
        final UUID applicationId = CDCEventsProducer.publishIncompleteApplication();
        Awaitility.await().during(10, TimeUnit.SECONDS);

        assertFalse(isMessageProcessed(applicationId));
    }

    @Transactional
    public boolean isMessageProcessed(UUID applicationId) {
        final ApplicationEntity applicationEntity = applicationsRepository.findById(applicationId);
        OutboxEntity outboxEvent = outboxRepository.find("aggregateId",applicationId.toString()).firstResult();

        if (applicationEntity != null && outboxEvent != null) {
            assertEquals(applicationId, applicationEntity.getId());
            assertEquals(CDCEventsProducer.APPLICATION_NAME, applicationEntity.getName());
            assertEquals(CDCEventsProducer.APPLICATION_EMAIL, applicationEntity.getEmail());
            assertEquals(CDCEventsProducer.APPLICATION_PHONE, applicationEntity.getPhone());
            assertEquals(CDCEventsProducer.APPLICATION_CV, applicationEntity.getCv());
            assertEquals(CDCEventsProducer.APPLICATION_POSITION_ID, applicationEntity.getPositionId());

            assertEquals(NEW_APPLICATION_RECEIVED_EVENT_TYPE, outboxEvent.getAggregateType());
            assertEquals(NEW_APPLICATION_RECEIVED_EVENT, outboxEvent.getType());
            assertNotNull(outboxEvent.getPayload());

            final NewApplicationReceivedDTO newApplicationReceivedDTO = outboxEvent.getPayload();
            assertEquals(applicationId, newApplicationReceivedDTO.getId());
            assertEquals(CDCEventsProducer.APPLICATION_NAME, newApplicationReceivedDTO.getName());
            assertEquals(CDCEventsProducer.APPLICATION_EMAIL, newApplicationReceivedDTO.getEmail());
            assertEquals(CDCEventsProducer.APPLICATION_PHONE, newApplicationReceivedDTO.getPhone());
            assertEquals(CDCEventsProducer.APPLICATION_CV, newApplicationReceivedDTO.getCv());
            assertEquals(CDCEventsProducer.APPLICATION_POSITION_ID, applicationEntity.getPositionId());

            return true;
        }

        return false;
    }
}
