package com.jaruiz.casarrubios.candidates.services.positions;



import java.time.Duration;

import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest.dto.PaginatedPositionsDTO;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest.dto.PositionDetailDTO;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.PostgresRepository;
import com.jaruiz.casarrubios.candidates.services.positions.utils.NewPositionsProducer;
import com.jaruiz.casarrubios.candidates.services.positions.utils.SetUpTopics;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;
import static com.jaruiz.casarrubios.candidates.services.positions.utils.AssertUtils.assertPaginatedPosition;
import static com.jaruiz.casarrubios.candidates.services.positions.utils.AssertUtils.assertPositionDetailDTO;
import static com.jaruiz.casarrubios.candidates.services.positions.utils.SetUpTopics.createKafkaTopics;
import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PositionsServiceIT {
    private static final Logger logger = LoggerFactory.getLogger(PositionsServiceIT.class);

    @Autowired
    private NewPositionsProducer newPositionsProducer;
    @Autowired
    private PostgresRepository postgresRepository;

    @LocalServerPort
    private Integer port;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:14-alpine"
    );

    @Container
    static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.6.0")
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        kafka.start();

        while (!kafka.isRunning() && !kafka.isHealthy()) {
            logger.info("Waiting for Kafka to start...");
        }

        createKafkaTopics(kafka.getBootstrapServers());
        await()
            .pollInterval(Duration.ofSeconds(3))
            .atMost(10, SECONDS)
            .untilAsserted(() -> {
                assertTrue(SetUpTopics.isCreated());
            });
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
        kafka.stop();
    }

    @BeforeEach
    void setUp() {
        postgresRepository.deleteAll();
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Test
    @Order(1)
    public void givenANewPosition_whenItIsPublishedToKafka_thenPositionIsStoredAndItsDetailCanBeConsulted() {
        long positionId = 1;
        newPositionsProducer.publishPositionPublishedEvent(positionId);
        final Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/positions/" + positionId);

        await()
            .pollInterval(Duration.ofSeconds(3))
            .atMost(30, SECONDS)
            .untilAsserted(() -> {
                final PositionDetailDTO positionDTO = response.getBody().as(PositionDetailDTO.class);
                assertNotNull(positionDTO);
                assertEquals(positionId, positionDTO.getId());
                assertPositionDetailDTO(positionDTO);
            });
    }

    @Test
    @Order(2)
    public void givenSomePositions_whenGetAllPositions_thenListOfPositionsIsReturned() {
        int totalPositionsToPublish = 43;
        for (int i = 0; i < totalPositionsToPublish; i++) {
            newPositionsProducer.publishPositionPublishedEvent(i + 1);
        }

        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/positions?page=0&size=10");

        PaginatedPositionsDTO paginatedPositionsDTO = response.getBody().as(PaginatedPositionsDTO.class);
        assertEquals(200, response.getStatusCode());
        assertPaginatedPosition(totalPositionsToPublish, 0, 10, paginatedPositionsDTO);

        response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/positions?page=4&size=10");

        final PaginatedPositionsDTO lastPageDTO = response.getBody().as(PaginatedPositionsDTO.class);
        assertEquals(200, response.getStatusCode());
        assertNotNull(lastPageDTO);
        assertEquals(4, lastPageDTO.getNumber());
        assertEquals(3, lastPageDTO.getContent().size());
    }

    @Test
    public void givenAnInvalidPositionId_whenGetPositionDetail_then404IsReceived() {
        final Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/positions/99");

        Assertions.assertEquals(404, response.getStatusCode());
    }

    @Test
    public void givenAnInvalidPositionId_whenGetPositionDetail_then400IsReceived() {
        final Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/positions/null");

        Assertions.assertEquals(400, response.getStatusCode());
    }


}
