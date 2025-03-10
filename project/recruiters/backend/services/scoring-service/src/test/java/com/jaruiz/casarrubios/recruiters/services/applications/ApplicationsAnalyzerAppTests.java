package com.jaruiz.casarrubios.recruiters.services.applications;

import java.io.FileInputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaruiz.casarrubios.recruiters.services.applications.api.async.dto.ApplicationAnalysedEventDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.api.async.dto.ApplicationAnalysisFailedEventDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.api.async.dto.ResumeAnalysisDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.util.kafka.ApplicationAnalysedEventConsumer;
import com.jaruiz.casarrubios.recruiters.services.applications.util.kafka.ApplicationsProcessedProducer;
import com.jaruiz.casarrubios.recruiters.services.applications.util.kafka.SetUpTopics;
import com.jaruiz.casarrubios.recruiters.services.applications.util.openai.TestOpenAiConfig;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;
import static com.jaruiz.casarrubios.recruiters.services.applications.api.async.ApplicationsAnalyzerAsyncAPI.ERROR_RESUME_NOT_FOUND;
import static com.jaruiz.casarrubios.recruiters.services.applications.util.kafka.SetUpTopics.createKafkaTopics;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ContextConfiguration(classes = { TestOpenAiConfig.class})
class ApplicationsAnalyzerAppTests {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationsAnalyzerAppTests.class);

    @Container
    static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.6.0")
    );

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:14-alpine"
    );

    static MinIOContainer minio = new MinIOContainer(DockerImageName.parse("minio/minio:latest"));

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("minio.url", minio::getS3URL);
        registry.add("minio.access.name", minio::getUserName);
        registry.add("minio.access.secret", minio::getPassword);
        registry.add("cv.bucket.name", ApplicationsAnalyzerAppTests::getBucketName);
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired ApplicationsProcessedProducer applicationsProcessedProducer;
    @Autowired ApplicationAnalysedEventConsumer applicationAnalysedEventConsumer;
    @Autowired MinioClient minioClient;

    @Mock
    private OpenAiChatModel chatClient;

    @BeforeAll
    static void beforeAll() {
        kafka.start();
        postgres.start();
        minio.start();
        while (!kafka.isRunning() && !kafka.isHealthy()) {
            logger.info("Waiting for Kafka to start...");
        }

        createKafkaTopics(kafka.getBootstrapServers());
       while(!SetUpTopics.isCreated()) {
           logger.info("Waiting for topics to be created...");
       }

    }

    @AfterAll
    static void afterAll() {
        kafka.stop();
        postgres.stop();
        minio.stop();
    }

    @Test
    void givenAnApplicationResumeSavedInStorageSystem_whenTheApplicationReceivedEventIsConsumed_ThenAnalysisIsDoneAndApplicationAnalysedEventIsPublished() throws Exception {
        final UUID applicationId = uploadCV("src/test/resources/cv.pdf");
        applicationsProcessedProducer.publishApplicationProcessedEvent(applicationId, 7L);

        Awaitility.await()
                  .atMost(120, TimeUnit.SECONDS)
                  .until(() -> applicationAnalysedEventConsumer.isApplicationAnalysedEventPublished());

        final ApplicationAnalysedEventDTO applicationAnalysedEventDTO = applicationAnalysedEventConsumer.getApplicationAnalysedEventDTO();

        assertNotNull(applicationAnalysedEventDTO);
        assertEquals(applicationId, applicationAnalysedEventDTO.getApplicationId());
        assertNotNull(applicationAnalysedEventDTO.getAnalysis());
        assertTrue(applicationAnalysedEventDTO.getAnalysis().getConcerns() != null && !applicationAnalysedEventDTO.getAnalysis().getConcerns().isEmpty());
        assertTrue(applicationAnalysedEventDTO.getAnalysis().getStrengths() != null && !applicationAnalysedEventDTO.getAnalysis().getStrengths().isEmpty());
        assertTrue(applicationAnalysedEventDTO.getAnalysis().getHardSkills() != null && !applicationAnalysedEventDTO.getAnalysis().getHardSkills().isEmpty());
        assertTrue(applicationAnalysedEventDTO.getAnalysis().getSoftSkills() != null && !applicationAnalysedEventDTO.getAnalysis().getSoftSkills().isEmpty());
        assertTrue(applicationAnalysedEventDTO.getAnalysis().getKeyResponsibilities() != null && !applicationAnalysedEventDTO.getAnalysis().getKeyResponsibilities().isEmpty());
        assertTrue(applicationAnalysedEventDTO.getAnalysis().getInterviewQuestions() != null && !applicationAnalysedEventDTO.getAnalysis().getInterviewQuestions().isEmpty());
        assertTrue(applicationAnalysedEventDTO.getAnalysis().getTags() != null && !applicationAnalysedEventDTO.getAnalysis().getTags().isEmpty());
        assertNotNull(applicationAnalysedEventDTO.getAnalysis().getSummary());
        assertTrue(applicationAnalysedEventDTO.getAnalysis().getAveragePermanency() > 0);
        assertTrue(applicationAnalysedEventDTO.getAnalysis().getTotalYearsExperience() > 0);
    }

    @Test
    void givenAnApplicationResumeNotSavedInStorageSystem_whenTheApplicationReceivedEventIsConsumed_ThenAnalysisIsNotDoneAndAnErrorEventIsReceived() throws Exception {
        Awaitility.await().during(10, TimeUnit.SECONDS);
        final UUID applicationId = UUID.randomUUID();
        applicationsProcessedProducer.publishApplicationProcessedEvent(applicationId, 7L);

        Awaitility.await()
                  .atMost(120, TimeUnit.SECONDS)
                  .until(() -> applicationAnalysedEventConsumer.isApplicationAnalysisFailedEventPublished());

        final ApplicationAnalysisFailedEventDTO applicationAnalysisFailedEventDTO = applicationAnalysedEventConsumer.getApplicationAnalysisFailedEventDTO();

        assertNotNull(applicationAnalysisFailedEventDTO);
        assertEquals(applicationId.toString(), applicationAnalysisFailedEventDTO.getApplicationId());
        assertNotNull(applicationAnalysisFailedEventDTO.getMessage());
        assertNotNull(applicationAnalysisFailedEventDTO.getCode());
        assertEquals(ERROR_RESUME_NOT_FOUND, applicationAnalysisFailedEventDTO.getCode());
    }

    private UUID uploadCV(String cv) throws Exception {
        final UUID applicationId = UUID.randomUUID();
        FileInputStream inputStream = new FileInputStream(cv);

        boolean bucketAlreadyCreated = minioClient.bucketExists(BucketExistsArgs.builder().bucket(getBucketName()).build());
        if (!bucketAlreadyCreated) {
            this.minioClient.makeBucket(MakeBucketArgs.builder().bucket(getBucketName()).build());
        }

        this.minioClient.putObject(
            PutObjectArgs.builder()
                         .bucket(getBucketName())
                         .object(applicationId.toString())
                         .stream(
                             inputStream, inputStream.available(), -1)
                         .contentType("application/pdf")
                         .build());

        return applicationId;
    }

    private static String getBucketName() {
        return "test";
    }
}
