package com.jaruiz.casarrubios.recruiters.services.globalposition;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import com.jaruiz.casarrubios.recruiters.services.globalposition.api.rest.GlobalPositionRestAPI;
import com.jaruiz.casarrubios.recruiters.services.globalposition.api.rest.dto.GlobalPositionDTO;
import com.jaruiz.casarrubios.recruiters.services.globalposition.infrastructure.config.config.TopologyConfig;
import com.jaruiz.casarrubios.recruiters.services.globalposition.util.containers.KafkaContainer;
import com.jaruiz.casarrubios.recruiters.services.globalposition.util.kafka.EventsProducer;
import com.jaruiz.casarrubios.recruiters.services.globalposition.util.kafka.KafkaAdminHelper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.apache.kafka.streams.KafkaStreams;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(KafkaContainer.class)
public class GlobalPositionTest {
    private static final Logger logger = Logger.getLogger(GlobalPositionRestAPI.class);

    @Inject TopologyConfig topologyConfig;
    @Inject EventsProducer eventsProducer;
    @Inject KafkaStreams streams;

    @BeforeEach
    public void setUp() {
        String bootstrapServers = KafkaContainer.getKafkaContainer().getBootstrapServers();
        List<String> topicNames = getTopicNames();
        KafkaAdminHelper.createTopics(bootstrapServers, topicNames);

        logger.info("Starting topology");
        Awaitility.await()
                  .atMost(Duration.ofSeconds(30))
                  .pollInterval(Duration.ofSeconds(1))
                  .until(() -> streams != null && streams.state() == KafkaStreams.State.RUNNING);


        logger.info("Topology started");
    }

    private List<String> getTopicNames() {
        return List.of(
            topologyConfig.getScoringTopic(),
            topologyConfig.getNewPositionsTopic(),
            topologyConfig.getNewApplicationsTopic());
    }

    @Test
    void whenANewPositionEventIsPublished_thenGlobalPositionIsUpdated() throws IOException {
        Response response = given().when().get("/global-position");
        final GlobalPositionDTO initialGlobalPositionDTO = response.getBody().as(GlobalPositionDTO.class);
        long totalPositions = initialGlobalPositionDTO.getTotalPositions();

        this.eventsProducer.publishNewPosition();
        Awaitility.await()
                  .atMost(Duration.ofSeconds(5))
                  .pollInterval(Duration.ofMillis(200))
                  .untilAsserted(() -> {
                      Response res = given().when().get("/global-position");
                      GlobalPositionDTO dto = res.getBody().as(GlobalPositionDTO.class);
                      assertEquals(totalPositions + 1, dto.getTotalPositions());
                  });
    }

    @Test
    void whenANewApplicationEventIsPublished_thenGlobalPositionIsUpdated() throws IOException {
        Response response = given().when().get("/global-position");
        final GlobalPositionDTO initialGlobalPositionDTO = response.getBody().as(GlobalPositionDTO.class);
        double averageApplications = initialGlobalPositionDTO.getAverageApplications();
        assertEquals(0.0, averageApplications);

        int totalPositions = 0;
        int totalApplications = 0;
        for (int i=0; i<2; i++) {
            this.eventsProducer.publishNewPosition();
            totalPositions++;
            for (int j=0; j<5; j++) {
                this.eventsProducer.publishApplicationReceived();
                totalApplications++;
            }
        }

        double expectedAverageApplications = (double) totalApplications / totalPositions;

        Awaitility.await()
                  .atMost(Duration.ofSeconds(5))
                  .pollInterval(Duration.ofMillis(200))
                  .untilAsserted(() -> {
                      Response res = given().when().get("/global-position");
                      GlobalPositionDTO dto = res.getBody().as(GlobalPositionDTO.class);
                      double averageApplicationsReceived = dto.getAverageApplications();
                      assertEquals(expectedAverageApplications, averageApplicationsReceived);
                  });
    }

    @Test
    void whenANewScoringEventIsPublished_thenGlobalPositionIsUpdated() throws IOException {
        Response response = given().when().get("/global-position");
        final GlobalPositionDTO initialGlobalPositionDTO = response.getBody().as(GlobalPositionDTO.class);
        double averageScore = initialGlobalPositionDTO.getAverageScore();
        assertEquals(0.0, averageScore);

        for (int i=0; i<20; i++) {
            this.eventsProducer.publishApplicationScored();
        }

        double expectedScore = 0.5;

        Awaitility.await()
                  .atMost(Duration.ofSeconds(5))
                  .pollInterval(Duration.ofMillis(200))
                  .untilAsserted(() -> {
                      Response res = given().when().get("/global-position");
                      GlobalPositionDTO dto = res.getBody().as(GlobalPositionDTO.class);
                      double averageScoreReceived = dto.getAverageScore();
                      assertEquals(expectedScore, averageScoreReceived);
                  });
    }

}
