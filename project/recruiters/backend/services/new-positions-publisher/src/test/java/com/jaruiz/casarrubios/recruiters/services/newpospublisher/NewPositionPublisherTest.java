package com.jaruiz.casarrubios.recruiters.services.newpospublisher;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.config.TopologyConfig;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.*;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.kafka.CDCDataProducer;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.containers.KafkaContainer;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.kafka.KafkaAdminHelper;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.kafka.PositionCompleteConsumer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(KafkaContainer.class)
class NewPositionPublisherTest {
    @Inject CDCDataProducer cdcDataProducer;
    @Inject PositionCompleteConsumer positionCompleteConsumer;
    @Inject TopologyConfig topologyConfig;

    @BeforeEach
    public void setUp() {
        String bootstrapServers = KafkaContainer.getKafkaContainer().getBootstrapServers();
        List<String> topicNames = getTopicNames();
        KafkaAdminHelper.createTopics(bootstrapServers, topicNames);
    }

    @AfterEach
    public void tearDown(){
        positionCompleteConsumer.reset();
    }

    @Test
    void whenARightPositionAndReqsAndTasksAndBenefitsAreReplicated_thenACompletePositionIsPublished() throws InterruptedException {
        long positionId = 1;

        cdcDataProducer.publishPosition(positionId);

        for (int i=0; i<5; i++) {
            cdcDataProducer.publishBenefit(positionId, i);
            cdcDataProducer.publishRequirement(positionId, i);
            cdcDataProducer.publishTask(positionId, i);
        }

        Awaitility.await()
                  .atMost(60, TimeUnit.SECONDS)
                  .until(() -> positionCompleteConsumer.isPositionCompletePublished());

        final Position positionComplete = positionCompleteConsumer.getPositionComplete();

        assertNotNull(positionComplete);
        assertEquals(positionId, positionComplete.getId());
        assertTrue(positionComplete.getRequirements() != null && !positionComplete.getRequirements().isEmpty());
        assertTrue(positionComplete.getBenefits() != null && !positionComplete.getBenefits().isEmpty());
        assertTrue(positionComplete.getTasks() != null && !positionComplete.getTasks().isEmpty());
        assertEquals(5, positionComplete.getRequirements().size());
        assertEquals(5, positionComplete.getBenefits().size());
        assertEquals(5, positionComplete.getTasks().size());
    }

    @Test
    void whenAnIncompletePositionWithoutReqsIsReplicated_thenACompletePositionIsNotPublished() throws InterruptedException {
        long positionId = 2;

        cdcDataProducer.publishPosition(positionId);

        for (int i=0; i<5; i++) {
            cdcDataProducer.publishBenefit(positionId, i);
            cdcDataProducer.publishTask(positionId, i);
        }

        Awaitility.await().during(30, TimeUnit.SECONDS);

        final Position positionComplete = positionCompleteConsumer.getPositionComplete();
        assertNull(positionComplete);
    }

    @Test
    void whenAnIncompletePositionWithoutTasksIsReplicated_thenACompletePositionIsNotPublished() throws InterruptedException {
        long positionId = 3;

        cdcDataProducer.publishPosition(positionId);

        for (int i=0; i<5; i++) {
            cdcDataProducer.publishBenefit(positionId, i);
            cdcDataProducer.publishRequirement(positionId, i);
        }

        Awaitility.await().during(30, TimeUnit.SECONDS);

        final Position positionComplete = positionCompleteConsumer.getPositionComplete();
        assertNull(positionComplete);
    }

    @Test
    void whenAnIncompletePositionWithoutBenefitsIsReplicated_thenACompletePositionIsNotPublished() throws InterruptedException {
        long positionId = 4;

        cdcDataProducer.publishPosition(positionId);

        for (int i=0; i<5; i++) {
            cdcDataProducer.publishRequirement(positionId, i);
            cdcDataProducer.publishTask(positionId, i);
        }

        Awaitility.await().during(30, TimeUnit.SECONDS);

        final Position positionComplete = positionCompleteConsumer.getPositionComplete();
        assertNull(positionComplete);
    }

    private List<String> getTopicNames() {
        return List.of(
            topologyConfig.getCdcPositionsTopic(),
            topologyConfig.getCdcPositionsRequirementsTopic(),
            topologyConfig.getCdcPositionsTaskTopic(),
            topologyConfig.getCdcPositionsBenefitsTopic());
    }
}
