package com.jaruiz.casarrubios.recruiters.services;

import java.util.ArrayList;
import java.util.List;

import com.jaruiz.casarrubios.recruiters.services.posmanager.api.rest.dto.*;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class PosManagerIT {

    @Test
    public void givenSomePositions_whenGetAllPositions_thenListOfPositionsIsReturned() {
        final Response response = given()
            .when().get("/positions");

        final PositionDTO[] positions = response.getBody().as(PositionDTO[].class);
        assertNotNull(positions);
        assertEquals(2, positions.length);
        for (PositionDTO position : positions) {
            checkPosition(position);
        }
    }

    @Test
    public void givenAValidIdOfACreatedPosition_whenGetPositionById_thenPositionIsReturned() {
        final Response response = given()
            .when().get("/positions/1");

        final PositionDetailDTO position = response.getBody().as(PositionDetailDTO.class);
        checkPositionDetail(position);
    }

    @Test
    public void givenAValidIdOfAPublishedPosition_whenGetPositionById_thenPositionIsReturned() {
        final Response response = given()
            .when().get("/positions/2");

        final PositionDetailDTO position = response.getBody().as(PositionDetailDTO.class);
        checkPositionDetail(position);
        assertNotNull(position.getPublishedAt());
    }

    @Test
    public void givenAInvalidId_whenGetPositionById_thenAnErrorIsThrown() {
        final Response response = given()
            .when().get("/positions/4");

        final ApplicationErrorDTO error = response.getBody().as(ApplicationErrorDTO.class);
        assertNotNull(error);
        assertNotNull(error.getApplicationId());
        assertNotNull(error.getErrorCode());
    }

    @Test
    public void givenPositionData_whenCreatedPosition_thenANewPositionIsCreated() {
//        private Long id;
//        private String title;
//        private String description;
//        private Integer status;
//        private @Valid List<@Valid RequirementDTO> requirements = new ArrayList<>();
//        private @Valid List<@Valid BenefitDTO> benefits = new ArrayList<>();
//        private @Valid List<@Valid TaskDTO> tasks = new ArrayList<>();

//        JsonObject jsonObject =
//            Json.createObjectBuilder()
//                .add("title", "ThirdMovie")
//                .add("description", "MyThirdMovie")
//                .add("director", "Me")
//                .add("country", "Planet")
//                .build();
//
//
//        given()
//            .contentType(ContentType.JSON)
//            .accept(ContentType.JSON)
//            .body("{\"name\": \"John Doe\"}")
//            .when()
//            .post("/hello")
//            .then()
//            .statusCode(201)
//            .body("workflowdata.greeting", equalTo("Hello, John Doe"));




        final Response response = given()
            .when().get("/positions/4");

        final ApplicationErrorDTO error = response.getBody().as(ApplicationErrorDTO.class);
        assertNotNull(error);
        assertNotNull(error.getApplicationId());
        assertNotNull(error.getErrorCode());
    }

    private void checkPosition(PositionDTO position) {
        assertNotNull(position);
        assertNotNull(position.getId());
        assertNotNull(position.getTitle());
        assertNotNull(position.getDescription());
        assertNotNull(position.getStatus());
    }

    private void checkPositionDetail(PositionDetailDTO position) {
        assertNotNull(position);
        assertNotNull(position.getId());
        assertNotNull(position.getTitle());
        assertNotNull(position.getDescription());
        assertNotNull(position.getStatus());
        assertNotNull(position.getCreatedAt());

        var tasks = position.getTasks();
        assertNotNull(tasks);
        tasks.forEach(task -> {
            assertNotNull(task.getDescription());
        });

        var requirements = position.getRequirements();
        assertNotNull(requirements);
        requirements.forEach(requirement -> {
            assertNotNull(requirement.getDescription());
            assertNotNull(requirement.getKey());
            assertNotNull(requirement.getValue());
            assertNotNull(requirement.getIsMandatory());
        });

        var benefits = position.getBenefits();
        assertNotNull(position.getBenefits());
        benefits.forEach(benefit -> {
            assertNotNull(benefit.getDescription());
        });
    }

}
