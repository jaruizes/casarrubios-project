package com.jaruiz.casarrubios.recruiters.services;

import java.util.ArrayList;
import java.util.List;

import com.jaruiz.casarrubios.recruiters.services.posmanager.api.rest.dto.*;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PosManagerIT {

    @Test
    @Order(1)
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
    @Order(2)
    public void givenAValidIdOfACreatedPosition_whenGetPositionById_thenPositionIsReturned() {
        final Response response = given()
            .when().get("/positions/1");

        final PositionDetailDTO position = response.getBody().as(PositionDetailDTO.class);
        checkPositionDetail(position);
    }

    @Test
    @Order(3)
    public void givenAValidIdOfAPublishedPosition_whenGetPositionById_thenPositionIsReturned() {
        final Response response = given()
            .when().get("/positions/2");

        final PositionDetailDTO position = response.getBody().as(PositionDetailDTO.class);
        checkPositionDetail(position);
        assertNotNull(position.getPublishedAt());
    }

    @Test
    @Order(4)
    public void givenAInvalidId_whenGetPositionById_thenAnErrorIsThrown() {
        final Response response = given()
            .when().get("/positions/4");

        final ApplicationErrorDTO error = response.getBody().as(ApplicationErrorDTO.class);
        assertNotNull(error);
        assertNotNull(error.getApplicationId());
        assertNotNull(error.getErrorCode());
    }

    @Test
    public void givenPositionData_whenCreatePosition_thenANewPositionIsCreated() {
        final List<RequirementDTO> requirementDTOS = new ArrayList<>();
        requirementDTOS.add(new RequirementDTO().key("Key 1").description("Requirement 1").value("Value 1").isMandatory(true));
        requirementDTOS.add(new RequirementDTO().key("Key 2").description("Requirement 2").value("Value 2").isMandatory(true));

        final List<TaskDTO> taskDTOS = new ArrayList<>();
        taskDTOS.add(new TaskDTO().description("Task 1"));
        taskDTOS.add(new TaskDTO().description("Task 2"));

        final List<BenefitDTO> benefitDTOS = new ArrayList<>();
        benefitDTOS.add(new BenefitDTO().description("Benefit 1"));
        benefitDTOS.add(new BenefitDTO().description("Benefit 2"));
        benefitDTOS.add(new BenefitDTO().description("Benefit 3"));

        final NewPositionDataDTO newPositionData = new NewPositionDataDTO();
        newPositionData.setTitle("New Position");
        newPositionData.setDescription("Description of the new position");
        newPositionData.setRequirements(requirementDTOS);
        newPositionData.setTasks(taskDTOS);
        newPositionData.setBenefits(benefitDTOS);

        final Response response = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(newPositionData)
            .when()
            .post("/positions");

        final PositionDetailDTO position = response.getBody().as(PositionDetailDTO.class);
        checkPositionDetail(position);
        assertEquals(2, position.getRequirements().size());
        assertEquals(2, position.getTasks().size());
        assertEquals(3, position.getBenefits().size());
    }

    @Test
    public void givenAPositionDataWithoutTitle_whenCreatePosition_thenAnErrorIsThrown() {
        final List<RequirementDTO> requirementDTOS = new ArrayList<>();
        requirementDTOS.add(new RequirementDTO().key("Key 1").description("Requirement 1").value("Value 1").isMandatory(true));
        requirementDTOS.add(new RequirementDTO().key("Key 2").description("Requirement 2").value("Value 2").isMandatory(true));

        final List<TaskDTO> taskDTOS = new ArrayList<>();
        taskDTOS.add(new TaskDTO().description("Task 1"));
        taskDTOS.add(new TaskDTO().description("Task 2"));

        final List<BenefitDTO> benefitDTOS = new ArrayList<>();
        benefitDTOS.add(new BenefitDTO().description("Benefit 1"));
        benefitDTOS.add(new BenefitDTO().description("Benefit 2"));
        benefitDTOS.add(new BenefitDTO().description("Benefit 3"));

        final NewPositionDataDTO newPositionDataWithoutTitle = new NewPositionDataDTO();
        newPositionDataWithoutTitle.setDescription("Description of the new position");
        newPositionDataWithoutTitle.setRequirements(requirementDTOS);
        newPositionDataWithoutTitle.setTasks(taskDTOS);
        newPositionDataWithoutTitle.setBenefits(benefitDTOS);

        final Response response = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(newPositionDataWithoutTitle)
            .when()
            .post("/positions");

        final ApplicationErrorDTO error = response.getBody().as(ApplicationErrorDTO.class);
        assertNotNull(error);
        assertNotNull(error.getApplicationId());
        assertNotNull(error.getErrorCode());
    }

    @Test
    public void givenAPositionDataWithoutDescription_whenCreatePosition_thenAnErrorIsThrown() {
        final List<RequirementDTO> requirementDTOS = new ArrayList<>();
        requirementDTOS.add(new RequirementDTO().key("Key 1").description("Requirement 1").value("Value 1").isMandatory(true));
        requirementDTOS.add(new RequirementDTO().key("Key 2").description("Requirement 2").value("Value 2").isMandatory(true));

        final List<TaskDTO> taskDTOS = new ArrayList<>();
        taskDTOS.add(new TaskDTO().description("Task 1"));
        taskDTOS.add(new TaskDTO().description("Task 2"));

        final List<BenefitDTO> benefitDTOS = new ArrayList<>();
        benefitDTOS.add(new BenefitDTO().description("Benefit 1"));
        benefitDTOS.add(new BenefitDTO().description("Benefit 2"));
        benefitDTOS.add(new BenefitDTO().description("Benefit 3"));

        final NewPositionDataDTO newPositionDataWithoutTitle = new NewPositionDataDTO();
        newPositionDataWithoutTitle.setTitle("New Position");
        newPositionDataWithoutTitle.setRequirements(requirementDTOS);
        newPositionDataWithoutTitle.setTasks(taskDTOS);
        newPositionDataWithoutTitle.setBenefits(benefitDTOS);

        final Response response = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(newPositionDataWithoutTitle)
            .when()
            .post("/positions");

        final ApplicationErrorDTO error = response.getBody().as(ApplicationErrorDTO.class);
        assertNotNull(error);
        assertNotNull(error.getApplicationId());
        assertNotNull(error.getErrorCode());
    }

    @Test
    public void givenAPositionDataWithoutReqs_whenCreatePosition_thenAnErrorIsThrown() {
//        final List<RequirementDTO> requirementDTOS = new ArrayList<>();
//        requirementDTOS.add(new RequirementDTO().key("Key 1").description("Requirement 1").value("Value 1").isMandatory(true));
//        requirementDTOS.add(new RequirementDTO().key("Key 2").description("Requirement 2").value("Value 2").isMandatory(true));

        final List<TaskDTO> taskDTOS = new ArrayList<>();
        taskDTOS.add(new TaskDTO().description("Task 1"));
        taskDTOS.add(new TaskDTO().description("Task 2"));

        final List<BenefitDTO> benefitDTOS = new ArrayList<>();
        benefitDTOS.add(new BenefitDTO().description("Benefit 1"));
        benefitDTOS.add(new BenefitDTO().description("Benefit 2"));
        benefitDTOS.add(new BenefitDTO().description("Benefit 3"));

        final NewPositionDataDTO newPositionDataWithoutTitle = new NewPositionDataDTO();
        newPositionDataWithoutTitle.setTitle("New Position");
        newPositionDataWithoutTitle.setDescription("Description of the new position");
//        newPositionDataWithoutTitle.setRequirements(requirementDTOS);
        newPositionDataWithoutTitle.setTasks(taskDTOS);
        newPositionDataWithoutTitle.setBenefits(benefitDTOS);

        final Response response = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(newPositionDataWithoutTitle)
            .when()
            .post("/positions");

        final ApplicationErrorDTO error = response.getBody().as(ApplicationErrorDTO.class);
        assertNotNull(error);
        assertNotNull(error.getApplicationId());
        assertNotNull(error.getErrorCode());
    }

    @Test
    public void givenAPositionDataWithoutTasks_whenCreatePosition_thenAnErrorIsThrown() {
        final List<RequirementDTO> requirementDTOS = new ArrayList<>();
        requirementDTOS.add(new RequirementDTO().key("Key 1").description("Requirement 1").value("Value 1").isMandatory(true));
        requirementDTOS.add(new RequirementDTO().key("Key 2").description("Requirement 2").value("Value 2").isMandatory(true));

//        final List<TaskDTO> taskDTOS = new ArrayList<>();
//        taskDTOS.add(new TaskDTO().description("Task 1"));
//        taskDTOS.add(new TaskDTO().description("Task 2"));

        final List<BenefitDTO> benefitDTOS = new ArrayList<>();
        benefitDTOS.add(new BenefitDTO().description("Benefit 1"));
        benefitDTOS.add(new BenefitDTO().description("Benefit 2"));
        benefitDTOS.add(new BenefitDTO().description("Benefit 3"));

        final NewPositionDataDTO newPositionDataWithoutTitle = new NewPositionDataDTO();
        newPositionDataWithoutTitle.setTitle("New Position");
        newPositionDataWithoutTitle.setDescription("Description of the new position");
        newPositionDataWithoutTitle.setRequirements(requirementDTOS);
//        newPositionDataWithoutTitle.setTasks(taskDTOS);
        newPositionDataWithoutTitle.setBenefits(benefitDTOS);

        final Response response = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(newPositionDataWithoutTitle)
            .when()
            .post("/positions");

        final ApplicationErrorDTO error = response.getBody().as(ApplicationErrorDTO.class);
        assertNotNull(error);
        assertNotNull(error.getApplicationId());
        assertNotNull(error.getErrorCode());
    }

    @Test
    public void givenAPositionDataWithoutBenefits_whenCreatePosition_thenAnErrorIsThrown() {
        final List<RequirementDTO> requirementDTOS = new ArrayList<>();
        requirementDTOS.add(new RequirementDTO().key("Key 1").description("Requirement 1").value("Value 1").isMandatory(true));
        requirementDTOS.add(new RequirementDTO().key("Key 2").description("Requirement 2").value("Value 2").isMandatory(true));

        final List<TaskDTO> taskDTOS = new ArrayList<>();
        taskDTOS.add(new TaskDTO().description("Task 1"));
        taskDTOS.add(new TaskDTO().description("Task 2"));

//        final List<BenefitDTO> benefitDTOS = new ArrayList<>();
//        benefitDTOS.add(new BenefitDTO().description("Benefit 1"));
//        benefitDTOS.add(new BenefitDTO().description("Benefit 2"));
//        benefitDTOS.add(new BenefitDTO().description("Benefit 3"));

        final NewPositionDataDTO newPositionDataWithoutTitle = new NewPositionDataDTO();
        newPositionDataWithoutTitle.setTitle("New Position");
        newPositionDataWithoutTitle.setDescription("Description of the new position");
        newPositionDataWithoutTitle.setRequirements(requirementDTOS);
        newPositionDataWithoutTitle.setTasks(taskDTOS);
//        newPositionDataWithoutTitle.setBenefits(benefitDTOS);

        final Response response = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(newPositionDataWithoutTitle)
            .when()
            .post("/positions");

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
