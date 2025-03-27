package com.jaruiz.casarrubios.recruiters.services.newpospublisher;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.config.TopologyConfig;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.Position;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.containers.KafkaContainer;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.kafka.CDCDataProducer;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.kafka.KafkaAdminHelper;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.kafka.PositionCompleteConsumer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.shaded.org.awaitility.Durations;
import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.bouncycastle.oer.its.template.ieee1609dot2.basetypes.Ieee1609Dot2BaseTypes.Duration;

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
    void whenARightPositionAndReqsAndTasksAndBenefitsAreReplicated_thenACompletePositionIsPublished() throws  IOException {
        long positionId = 1;

        cdcDataProducer.publishPosition(positionId, true, true, true);

        Awaitility.await()
                  .atMost(200, TimeUnit.SECONDS)
                  .until(() -> positionCompleteConsumer.isPositionCompletePublished());

        final Position positionComplete = positionCompleteConsumer.getPositionComplete();

        assertNotNull(positionComplete);
        assertEquals(positionId, positionComplete.getId());
        assertTrue(positionComplete.getRequirements() != null && !positionComplete.getRequirements().isEmpty());
        assertTrue(positionComplete.getBenefits() != null && !positionComplete.getBenefits().isEmpty());
        assertTrue(positionComplete.getTasks() != null && !positionComplete.getTasks().isEmpty());
        assertEquals(3, positionComplete.getRequirements().size());
        assertEquals(3, positionComplete.getBenefits().size());
        assertEquals(4, positionComplete.getTasks().size());
    }

    @Test
    void whenAnIncompletePositionWithoutReqsIsReplicated_thenACompletePositionIsNotPublished() throws IOException {
        cdcDataProducer.publishPosition(2L, false, true, true);

        Awaitility.await().pollDelay(Durations.FIVE_SECONDS).until(() -> true);

        final Position positionComplete = positionCompleteConsumer.getPositionComplete();
        assertNull(positionComplete);
    }

    @Test
    void whenAnIncompletePositionWithoutTasksIsReplicated_thenACompletePositionIsNotPublished() throws IOException {
        cdcDataProducer.publishPosition(3L, true, false, true);

        Awaitility.await().pollDelay(Durations.FIVE_SECONDS).until(() -> true);

        final Position positionComplete = positionCompleteConsumer.getPositionComplete();
        assertNull(positionComplete);
    }

    @Test
    void whenAnIncompletePositionWithoutBenefitsIsReplicated_thenACompletePositionIsNotPublished() throws IOException {
        cdcDataProducer.publishPosition(4L, true, true, false);

        Awaitility.await().pollDelay(Durations.FIVE_SECONDS).until(() -> true);

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
