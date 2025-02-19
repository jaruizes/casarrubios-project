package com.jaruiz.casarrubios.candidates.services.applications;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.jaruiz.casarrubios.candidates.services.applications.adapters.api.rest.ExceptionHandlerController;
import com.jaruiz.casarrubios.candidates.services.applications.business.ApplicationsService;
import com.jaruiz.casarrubios.candidates.services.applications.adapters.api.rest.dto.ApplicationErrorDTO;
import com.jaruiz.casarrubios.candidates.services.applications.adapters.api.rest.dto.ApplicationResponseDTO;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationsServiceApplicationTests {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:16-alpine"
    );
    static MinIOContainer minio = new MinIOContainer(DockerImageName.parse("minio/minio:latest"));

    static DynamicPropertyRegistry registry;

    static String getBucketName() {
        return "test";
    }

    @LocalServerPort
    private Integer port;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        minio.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
        minio.stop();
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        ApplicationsServiceApplicationTests.registry = registry;
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("minio.url", minio::getS3URL);
        registry.add("minio.access.name", minio::getUserName);
        registry.add("minio.access.secret", minio::getPassword);
        registry.add("cv.bucket.name", ApplicationsServiceApplicationTests::getBucketName);
    }

    @Test
    void givenAResumeAndCandidateData_whenUpload_thenAnApplicationIsCreated() throws IOException {
        final Response response = given()
            .contentType("multipart/form-data")
            .multiPart("cvFile", "Fictional_AI_Expert_CV_Spanish.pdf", getFile(), "application/pdf")
            .multiPart("positionId", "1")
            .multiPart("candidate", "{\"name\": \"John\",\"email\": \"john.doe@example.com\",\"phone\": \"1234567890\"}", "application/json")
            .when()
            .post("/applications");

        final ApplicationResponseDTO applicationResponseDTO = response.getBody().as(ApplicationResponseDTO.class);
        assertEquals(201, response.getStatusCode());
        assertNotNull(applicationResponseDTO);
        assertTrue(applicationResponseDTO.getApplicationId() != null && !applicationResponseDTO.getApplicationId().isEmpty());
        assertTrue(applicationResponseDTO.getPosition() != null && applicationResponseDTO.getPosition() > 0);

    }

    @Test
    void givenAResumeAndNoPositionData_whenUpload_thenA400isReceived() throws IOException {
        final Response response = given()
            .contentType("multipart/form-data")
            .multiPart("cvFile", "Fictional_AI_Expert_CV_Spanish.pdf", getFile(), "application/pdf")
            .multiPart("candidate", "{\"name\": \"John\",\"email\": \"john.doe@example.com\",\"phone\": \"1234567890\"}", "application/json")
            .when()
            .post("/applications");

        assertBadRequestMissingParams(response);
    }

    @Test
    void givenAInvalidResumeAndNoCompleteData_whenUpload_thenA400isReceived() throws IOException {
        final Response response = given()
            .contentType("multipart/form-data")
            .multiPart("positionId", "1")
            .multiPart("cvFile", "Fictional_AI_Expert_CV_Spanish.pdf", getFile(), "application/pdf")
            .when()
            .post("/applications");

        assertBadRequestMissingParams(response);
    }

    @Test
    void givenAInvalidResumeAndNoCandidateData_whenUpload_thenA400isReceived() throws IOException {
        final Response response = given()
            .contentType("multipart/form-data")
            .multiPart("positionId", "1")
            .when()
            .post("/applications");

        assertBadRequestMissingParams(response);
    }

    @Test
    void givenAResumeAndBadCandidateData_whenUpload_thenAnIncompleteErrorIsThrown() throws IOException {
        final Response response = given()
            .contentType("multipart/form-data")
            .multiPart("cvFile", "Fictional_AI_Expert_CV_Spanish.pdf", getFile(), "application/pdf")
            .multiPart("positionId", "1")
            .multiPart("candidate", "{\"name\": \"John\",\"phone\": \"1234567890\"}", "application/json")
            .when()
            .post("/applications");

        assertEquals(400, response.getStatusCode());
        final ApplicationErrorDTO applicationErrorDTO = response.getBody().as(ApplicationErrorDTO.class);
        assertNotNull(applicationErrorDTO);
        assertEquals(ExceptionHandlerController.APPLICACION_INCOMPLETE, applicationErrorDTO.getErrorCode());


    }

//    @Test
//    void givenAResumeACandidateDataAndNoDatabase_whenUpload_thenAMetadataErrorIsThrown() throws IOException {
//        postgres.stop();
//
//        final Response response = given()
//            .contentType("multipart/form-data")
//            .multiPart("cvFile", "Fictional_AI_Expert_CV_Spanish.pdf", getFile(), "application/pdf")
//            .multiPart("positionId", "1")
//            .multiPart("candidate", "{\"name\": \"John\",\"surname\": \"Doe\",\"email\": \"john.doe@example.com\",\"phone\": \"1234567890\"}", "application/json")
//            .when()
//            .post("/applications");
//
//        assertEquals(500, response.getStatusCode());
//        final ApplicationErrorDTO applicationErrorDTO = response.getBody().as(ApplicationErrorDTO.class);
//        assertNotNull(applicationErrorDTO);
//        assertNotNull(applicationErrorDTO.getApplicationId());
//        assertEquals(ApplicationsService.METADATA_NOT_STORED_ERROR, applicationErrorDTO.getErrorCode());
//    }

    @Test
    void givenAResumeACandidateDataAndNoMinioUp_whenUpload_thenAFileErrorIsThrown() throws IOException {
        minio.stop();

        final Response response = given()
            .contentType("multipart/form-data")
            .multiPart("cvFile", "Fictional_AI_Expert_CV_Spanish.pdf", getFile(), "application/pdf")
            .multiPart("positionId", "1")
            .multiPart("candidate", "{\"name\": \"John\",\"email\": \"john.doe@example.com\",\"phone\": \"1234567890\"}", "application/json")
            .when()
            .post("/applications");

        minio.start();

        assertEquals(500, response.getStatusCode());
        final ApplicationErrorDTO applicationErrorDTO = response.getBody().as(ApplicationErrorDTO.class);
        assertNotNull(applicationErrorDTO);
        assertNotNull(applicationErrorDTO.getApplicationId());
        assertEquals(ApplicationsService.FILE_NOT_STORED_ERROR, applicationErrorDTO.getErrorCode());

    }




    private static void assertBadRequestMissingParams(Response response) {
        assertEquals(400, response.getStatusCode());
        final ApplicationErrorDTO applicationErrorDTO = response.getBody().as(ApplicationErrorDTO.class);
        assertNotNull(applicationErrorDTO);
        assertEquals(ExceptionHandlerController.MISSING_PARAMS, applicationErrorDTO.getErrorCode());
    }

    private byte[] getFile() throws IOException {
        Path path = Paths.get("src/test/resources/Fictional_AI_Expert_CV_Spanish.pdf");
        return Files.readAllBytes(path);
    }

}
