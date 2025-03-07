package com.jaruiz.casarrubios.recruiters.services;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.jaruiz.casarrubios.recruiters.services.adapters.api.async.dto.ApplicationDTO;
import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.ApplicationsRepository;
import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.entities.ApplicationEntity;
import com.jaruiz.casarrubios.recruiters.services.util.containers.KafkaTestResource;
import com.jaruiz.casarrubios.recruiters.services.util.kafka.ApplicationProcessedConsumer;
import com.jaruiz.casarrubios.recruiters.services.util.kafka.ApplicationsProducer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(KafkaTestResource.class)
class ApplicationsUpdateServiceTest {

    @Inject ApplicationsProducer applicationsProducer;
    @Inject ApplicationProcessedConsumer applicationProcessedConsumer;
    @Inject ApplicationsRepository applicationsRepository;

    @Test
    void whenANewApplicationIsSubmited_thenTheApplicationIsCapturedAndStoredIntoDatabase() {
        final UUID applicationId = applicationsProducer.publishApplication();
        Awaitility.await()
                  .atMost(120, TimeUnit.SECONDS)
                  .until(() -> isMessageProcessed(applicationId));

        Awaitility.await()
                  .atMost(120, TimeUnit.SECONDS)
                  .until(() -> applicationProcessedConsumer.isApplicationProcessedEventPublished());

        final ApplicationDTO applicationProcessedDTO = applicationProcessedConsumer.getApplicationProcessedDTO();
        assertNotNull(applicationProcessedDTO);
        assertEquals(applicationId, applicationProcessedDTO.getId());
    }

    @Test
    void whenAWrongApplicationIsSubmited_thenTheApplicationIsNotProcessed() {
        final UUID applicationId = applicationsProducer.publishIncompleteApplication();
        Awaitility.await().during(10, TimeUnit.SECONDS);

        assertFalse(isMessageProcessed(applicationId));
    }

    @Transactional
    public boolean isMessageProcessed(UUID applicationId) {
        final ApplicationEntity applicationEntity = applicationsRepository.findById(applicationId);
        if (applicationsRepository.findById(applicationId) != null) {
            assertEquals(applicationId, applicationEntity.getId());
            assertEquals(ApplicationsProducer.APPLICATION_NAME, applicationEntity.getName());
            assertEquals(ApplicationsProducer.APPLICATION_EMAIL, applicationEntity.getEmail());
            assertEquals(ApplicationsProducer.APPLICATION_PHONE, applicationEntity.getPhone());
            assertEquals(ApplicationsProducer.APPLICATION_CV, applicationEntity.getCv());
            assertEquals(ApplicationsProducer.APPLICATION_POSITION_ID, applicationEntity.getPositionId());
            assertEquals(ApplicationsProducer.APPLICATION_CREATED_AT, applicationEntity.getCreatedAt().getTime());

            return true;
        }

        return false;
    }
}
