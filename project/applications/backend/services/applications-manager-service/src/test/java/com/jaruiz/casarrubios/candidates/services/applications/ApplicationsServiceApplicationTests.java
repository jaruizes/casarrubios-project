package com.jaruiz.casarrubios.candidates.services.applications;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.UUID;

import com.jaruiz.casarrubios.candidates.services.applications.adapters.api.rest.ExceptionHandlerController;
import com.jaruiz.casarrubios.candidates.services.applications.business.ApplicationsService;
import com.jaruiz.casarrubios.candidates.services.applications.adapters.api.rest.dto.ApplicationErrorDTO;
import com.jaruiz.casarrubios.candidates.services.applications.adapters.api.rest.dto.ApplicationResponseDTO;
import com.jaruiz.casarrubios.candidates.services.applications.infrastructure.config.Config;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final String NAME = "John Doe";
    private static final String EMAIL = "johndoe@email.com";
    private static final String PHONE = "1234567890";
    private static final String POSITION_ID = "1";

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:16-alpine"
    );
    static MinIOContainer minio = new MinIOContainer(DockerImageName.parse("minio/minio:latest"));

    static DynamicPropertyRegistry registry;

    static String getBucketName() {
        return "test";
    }

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private Config config;

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
            .multiPart("positionId", POSITION_ID)
            .multiPart("candidate", "{\"name\": \"" + NAME + "\",\"email\": \"" + EMAIL + "\",\"phone\": \"" + PHONE + "\"}", "application/json")
            .when()
            .post("/applications");

        final ApplicationResponseDTO applicationResponseDTO = response.getBody().as(ApplicationResponseDTO.class);
        assertEquals(201, response.getStatusCode());
        assertNotNull(applicationResponseDTO);
        assertTrue(applicationResponseDTO.getApplicationId() != null && !applicationResponseDTO.getApplicationId().isEmpty());
        assertTrue(applicationResponseDTO.getPosition() != null && applicationResponseDTO.getPosition() > 0);

        checkSavedData(applicationResponseDTO.getApplicationId());
        checkCVIsStored(applicationResponseDTO.getApplicationId());
    }

    @Test
    void givenAResumeAndNoPositionData_whenUpload_thenA400isReceived() throws IOException {
        final Response response = given()
            .contentType("multipart/form-data")
            .multiPart("cvFile", "Fictional_AI_Expert_CV_Spanish.pdf", getFile(), "application/pdf")
            .multiPart("candidate", "{\"name\": \"" + NAME + "\",\"email\": \"" + EMAIL + "\",\"phone\": \"" + PHONE + "\"}", "application/json")
            .when()
            .post("/applications");

        assertBadRequestMissingParams(response);
    }

    @Test
    void givenAInvalidResumeAndNoCompleteData_whenUpload_thenA400isReceived() throws IOException {
        final Response response = given()
            .contentType("multipart/form-data")
            .multiPart("positionId", POSITION_ID)
            .multiPart("cvFile", "Fictional_AI_Expert_CV_Spanish.pdf", getFile(), "application/pdf")
            .when()
            .post("/applications");

        assertBadRequestMissingParams(response);
    }

    @Test
    void givenAInvalidResumeAndNoCandidateData_whenUpload_thenA400isReceived() throws IOException {
        final Response response = given()
            .contentType("multipart/form-data")
            .multiPart("positionId", POSITION_ID)
            .when()
            .post("/applications");

        assertBadRequestMissingParams(response);
    }

    @Test
    void givenAResumeAndBadCandidateData_whenUpload_thenAnIncompleteErrorIsThrown() throws IOException {
        final Response response = given()
            .contentType("multipart/form-data")
            .multiPart("cvFile", "Fictional_AI_Expert_CV_Spanish.pdf", getFile(), "application/pdf")
            .multiPart("positionId", POSITION_ID)
            .multiPart("candidate", "{\"name\": \"" + NAME + "\",\"email\": \"" + EMAIL + "\"}", "application/json")
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
            .multiPart("positionId", POSITION_ID)
            .multiPart("candidate", "{\"name\": \"" + NAME + "\",\"email\": \"" + EMAIL + "\",\"phone\": \"" + PHONE + "\"}", "application/json")
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

    private void checkSavedData(String applicationId) {
        final String select = "SELECT ID, NAME, EMAIL, PHONE, CV, POSITION_ID, CREATED_AT FROM applications.applications WHERE id = '" + applicationId + "'";

        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            assertTrue(resultSet.next());
            assertEquals(applicationId, resultSet.getString("ID"));
            assertEquals(NAME, resultSet.getString("NAME"));
            assertEquals(EMAIL, resultSet.getString("EMAIL"));
            assertEquals(PHONE, resultSet.getString("PHONE"));
            assertEquals(Integer.valueOf(POSITION_ID), resultSet.getInt("POSITION_ID"));
            assertNotNull(resultSet.getTimestamp("CREATED_AT"));
            assertNotNull(resultSet.getString("CV"));
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    private void checkCVIsStored(String applicationId) {
        try {
            assertTrue(minioClient.bucketExists(BucketExistsArgs.builder().bucket(config.getBucketName()).build()));
            final GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder().bucket(config.getBucketName()).object(applicationId).build());
            assertNotNull(response);
            assertNotNull(response.object());
            byte[] content = response.readAllBytes();
            assertNotNull(content);
            assertTrue(content.length > 0);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
