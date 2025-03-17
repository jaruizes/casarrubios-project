package com.jaruiz.casarrubios.recruiters.services;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.jaruiz.casarrubios.recruiters.services.api.input.async.dto.ApplicationCDCDTO;
import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.ApplicationsRepository;
import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.entities.ApplicationEntity;
import com.jaruiz.casarrubios.recruiters.services.api.output.async.dto.NewApplicationReceivedDTO;
import com.jaruiz.casarrubios.recruiters.services.util.containers.KafkaTestResource;
import com.jaruiz.casarrubios.recruiters.services.util.kafka.ApplicationReceivedTestConsumer;
import com.jaruiz.casarrubios.recruiters.services.util.kafka.CDCEventsProducer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(KafkaTestResource.class)
class ApplicationsUpdateServiceTest {

    @Inject CDCEventsProducer CDCEventsProducer;
    @Inject ApplicationReceivedTestConsumer applicationReceivedTestConsumer;
    @Inject ApplicationsRepository applicationsRepository;

    @Test
    void whenANewApplicationIsSubmitted_thenTheApplicationIsCapturedAndStoredIntoDatabase() {
        final UUID applicationId = CDCEventsProducer.publishChangeEvent();
        Awaitility.await()
                  .atMost(120, TimeUnit.SECONDS)
                  .until(() -> isMessageProcessed(applicationId));

        Awaitility.await()
                  .atMost(120, TimeUnit.SECONDS)
                  .until(() -> applicationReceivedTestConsumer.isApplicationReceivedEventPublished());

        final NewApplicationReceivedDTO newApplicationReceivedDTO = applicationReceivedTestConsumer.getApplicationReceivedDTO();
        assertNotNull(newApplicationReceivedDTO);
        assertEquals(applicationId, newApplicationReceivedDTO.getId());
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
        if (applicationsRepository.findById(applicationId) != null) {
            assertEquals(applicationId, applicationEntity.getId());
            assertEquals(CDCEventsProducer.APPLICATION_NAME, applicationEntity.getName());
            assertEquals(CDCEventsProducer.APPLICATION_EMAIL, applicationEntity.getEmail());
            assertEquals(CDCEventsProducer.APPLICATION_PHONE, applicationEntity.getPhone());
            assertEquals(CDCEventsProducer.APPLICATION_CV, applicationEntity.getCv());
            assertEquals(CDCEventsProducer.APPLICATION_POSITION_ID, applicationEntity.getPositionId());
            assertEquals(CDCEventsProducer.APPLICATION_CREATED_AT, applicationEntity.getCreatedAt());

            return true;
        }

        return false;
    }
}
