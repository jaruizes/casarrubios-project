package com.jaruiz.casarrubios.candidates.services.positionsservice.it;

import java.util.Arrays;

import com.jaruiz.casarrubios.candidates.services.positionsservice.api.rest.dto.PositionDTO;
import com.jaruiz.casarrubios.candidates.services.positionsservice.api.rest.dto.PositionDetailDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PositionsServiceIT {

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:16-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    public void givenSomePositions_whenGetAllPositions_thenListOfPositionsIsReturned() {
        final Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/positions");

        final PositionDTO[] positions = response.getBody().as(PositionDTO[].class);
        Assertions.assertEquals(1, positions.length);
        Arrays.asList(positions).forEach(position -> {
            Assertions.assertTrue(position.getId() > 0);
            Assertions.assertTrue(position.getTitle() != null && !position.getTitle().isEmpty());
            Assertions.assertTrue(position.getDescription() != null && !position.getDescription().isEmpty());
        });
    }

    @Test
    public void givenAValidPositionId_whenGetPositionDetail_thenPositionIsRetrieved() {
        final Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/positions/1");

        final PositionDetailDTO position = response.getBody().as(PositionDetailDTO.class);
        Assertions.assertTrue(position.getId() > 0);
        Assertions.assertTrue(position.getTitle() != null && !position.getTitle().isEmpty());
        Assertions.assertTrue(position.getDescription() != null && !position.getDescription().isEmpty());

        position.getRequirements().forEach(requirement -> {
            Assertions.assertTrue(requirement.getDescription() != null && !requirement.getDescription().isEmpty());
        });

        position.getConditions().forEach(condition -> {
            Assertions.assertTrue(condition.getDescription() != null && !condition.getDescription().isEmpty());
        });
    }

    @Test
    public void givenAnInvalidPositionId_whenGetPositionDetail_then404IsReceived() {
        final Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/positions/2");

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