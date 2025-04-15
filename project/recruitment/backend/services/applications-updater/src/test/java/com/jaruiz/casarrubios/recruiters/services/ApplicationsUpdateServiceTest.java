package com.jaruiz.casarrubios.recruiters.services;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository.OutboxRepository;
import com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository.dto.NewApplicationReceivedEventDTO;
import com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository.entitites.OutboxEntity;
import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.ApplicationsRepository;
import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.CandidatesRepository;
import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.entities.ApplicationEntity;
import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.entities.CandidateEntity;
import com.jaruiz.casarrubios.recruiters.services.util.containers.KafkaTestResource;
import com.jaruiz.casarrubios.recruiters.services.util.kafka.CDCEventsProducer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
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
    @Inject CandidatesRepository candidatesRepository;
    @Inject OutboxRepository outboxRepository;
    @Inject EntityManager entityManager;

    @Test
    void whenANewApplicationIsSubmitted_thenTheApplicationIsCapturedAndStoredIntoDatabase() {
        final UUID applicationId = CDCEventsProducer.publishChangeEvent();
        Awaitility.await()
                  .atMost(200, TimeUnit.SECONDS)
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
        if (applicationEntity != null) {
            final CandidateEntity candidateEntity = candidatesRepository.findById(applicationEntity.getCandidateId());
            if (candidateEntity != null) {
                assertEquals(applicationId, applicationEntity.getId());
                assertNotNull(applicationEntity.getCandidateId());
                assertEquals(CDCEventsProducer.APPLICATION_POSITION_ID, applicationEntity.getPositionId());

                assertNotNull(candidateEntity.getId());
                assertEquals(CDCEventsProducer.APPLICATION_NAME, candidateEntity.getName());
                assertEquals(CDCEventsProducer.APPLICATION_EMAIL, candidateEntity.getEmail());
                assertEquals(CDCEventsProducer.APPLICATION_PHONE, candidateEntity.getPhone());
                assertEquals(CDCEventsProducer.APPLICATION_CV, candidateEntity.getCv());


//                assertEquals(NEW_APPLICATION_RECEIVED_EVENT_TYPE, outboxEvent.getAggregateType());
//                assertEquals(NEW_APPLICATION_RECEIVED_EVENT, outboxEvent.getType());
//                assertNotNull(outboxEvent.getPayload());
//
//                final NewApplicationReceivedEventDTO newApplicationReceivedEventDTO = outboxEvent.getPayload();
//                assertEquals(applicationId, newApplicationReceivedEventDTO.getId());
//                assertNotNull(newApplicationReceivedEventDTO.getCandidateId());
//                assertEquals(CDCEventsProducer.APPLICATION_POSITION_ID, applicationEntity.getPositionId());

                assertIsARowInOutboxTable();

                return true;
            }
        }


        return false;
    }

    private void assertIsARowInOutboxTable() {
        List<Object[]> rows = entityManager.createNativeQuery("SELECT * FROM recruiters.outbox").getResultList();

        assertFalse(rows.isEmpty(), "Expected outbox to have at least one row");
    }
}
