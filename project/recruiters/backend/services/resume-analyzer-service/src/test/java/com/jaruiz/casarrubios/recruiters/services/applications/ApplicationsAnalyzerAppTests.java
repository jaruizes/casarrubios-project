package com.jaruiz.casarrubios.recruiters.services.applications;

import java.io.FileInputStream;
import java.time.Duration;
import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.applications.api.output.async.dto.ApplicationAnalysedEventDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.api.output.async.dto.ApplicationAnalysisFailedEventDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.AnalysingException;
import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.TextExtractingException;
import com.jaruiz.casarrubios.recruiters.services.applications.util.kafka.ApplicationAnalysedEventConsumer;
import com.jaruiz.casarrubios.recruiters.services.applications.util.kafka.ApplicationsProcessedProducer;
import com.jaruiz.casarrubios.recruiters.services.applications.util.kafka.SetUpTopics;
import com.jaruiz.casarrubios.recruiters.services.applications.util.openai.SetupOpenAI;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;
import static com.jaruiz.casarrubios.recruiters.services.applications.api.input.async.ApplicationsAnalyzerAsyncAPI.ERROR_PROCESSING_APPLICATION_RECEIVED_EVENT;
import static com.jaruiz.casarrubios.recruiters.services.applications.business.ApplicationsAnalyzerService.ERROR_RESUME_NOT_FOUND;
import static com.jaruiz.casarrubios.recruiters.services.applications.util.kafka.SetUpTopics.createKafkaTopics;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ContextConfiguration(classes = { SetupOpenAI.class})
class ApplicationsAnalyzerAppTests {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationsAnalyzerAppTests.class);

    @Container
    static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.6.0")
    );

    static MinIOContainer minio = new MinIOContainer(DockerImageName.parse("minio/minio:latest"));

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("minio.url", minio::getS3URL);
        registry.add("minio.access.name", minio::getUserName);
        registry.add("minio.access.secret", minio::getPassword);
        registry.add("cv.bucket.name", ApplicationsAnalyzerAppTests::getBucketName);
    }

    @Autowired ApplicationsProcessedProducer applicationsProcessedProducer;
    @Autowired ApplicationAnalysedEventConsumer applicationAnalysedEventConsumer;
    @Autowired MinioClient minioClient;
    @Autowired OpenAiChatModel openAiChatModel;

    @BeforeAll
    static void beforeAll() throws InterruptedException {
        kafka.start();
        minio.start();
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
        kafka.stop();
        minio.stop();
    }

    @Test
    void givenAnApplicationResumeSavedInStorageSystem_whenTheApplicationReceivedEventIsConsumed_ThenAnalysisIsDoneAndApplicationAnalysedEventIsPublished() throws Exception {
        when(openAiChatModel.call(anyString())).thenReturn(SetupOpenAI.getFakeResponse());
        final UUID applicationId = uploadCV("src/test/resources/cv.pdf");
        applicationsProcessedProducer.publishApplicationReceivedEvent(applicationId, 7L);

        await()
            .pollInterval(Duration.ofSeconds(3))
            .atMost(30, SECONDS)
            .untilAsserted(() -> {
                assertTrue(applicationAnalysedEventConsumer.isApplicationAnalysedEventPublished());
                final ApplicationAnalysedEventDTO applicationAnalysedEventDTO = applicationAnalysedEventConsumer.getApplicationAnalysedEventDTO();

                assertNotNull(applicationAnalysedEventDTO);
                assertEquals(applicationId, applicationAnalysedEventDTO.getApplicationId());
                assertEquals(7L, applicationAnalysedEventDTO.getPositionId());
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
            });
    }

    @Test
    void givenAnApplicationResumeNotSavedInStorageSystem_whenTheApplicationReceivedEventIsConsumed_ThenAnalysisIsNotDoneAndAnErrorEventIsReceived() throws Exception {
        final UUID applicationId = UUID.randomUUID();
        applicationsProcessedProducer.publishApplicationReceivedEvent(applicationId, 7L);

        await()
            .pollInterval(Duration.ofSeconds(3))
            .atMost(30, SECONDS)
            .untilAsserted(() -> {
                assertTrue(applicationAnalysedEventConsumer.isApplicationAnalysisFailedEventPublished());

                final ApplicationAnalysisFailedEventDTO applicationAnalysisFailedEventDTO = applicationAnalysedEventConsumer.getApplicationAnalysisFailedEventDTO();
                assertNotNull(applicationAnalysisFailedEventDTO);
                assertEquals(applicationId.toString(), applicationAnalysisFailedEventDTO.getApplicationId());
                assertNotNull(applicationAnalysisFailedEventDTO.getMessage());
                assertNotNull(applicationAnalysisFailedEventDTO.getCode());
                assertEquals(ERROR_RESUME_NOT_FOUND, applicationAnalysisFailedEventDTO.getCode());
            });
    }

    @Test
    void givenAnApplicationReceivedEvent_whenTheApplicationReceivedEventIsConsumedAndOpenAIAnswerIsEmpty_ThenAnalysisIsNotDoneAndAnErrorEventIsPublishedToDLQ() throws Exception {
        when(openAiChatModel.call(anyString())).thenReturn(null);
        final UUID applicationId = uploadCV("src/test/resources/cv.pdf");
        applicationsProcessedProducer.publishApplicationReceivedEvent(applicationId, 7L);

        await()
            .pollInterval(Duration.ofSeconds(3))
            .atMost(30, SECONDS)
            .untilAsserted(() -> {
                assertTrue(applicationAnalysedEventConsumer.isApplicationAnalysisFailedEventPublished());

                final ApplicationAnalysisFailedEventDTO applicationAnalysisFailedEventDTO = applicationAnalysedEventConsumer.getApplicationAnalysisFailedEventDTO();
                assertNotNull(applicationAnalysisFailedEventDTO);
                assertEquals(applicationId.toString(), applicationAnalysisFailedEventDTO.getApplicationId());
                assertNotNull(applicationAnalysisFailedEventDTO.getMessage());
                assertNotNull(applicationAnalysisFailedEventDTO.getCode());
                assertEquals(AnalysingException.CODE_LLM_RESPONSE_NULL_OR_EMPTY, applicationAnalysisFailedEventDTO.getCode());
            });
    }

    @Test
    void givenAnApplicationReceivedEvent_whenTheEventIsConsumedButOpenAIAnswerIsWrong_ThenAnalysisIsNotDoneAndAnErrorEventIsPublishedToDLQ() throws Exception {
        when(openAiChatModel.call(anyString())).thenReturn(SetupOpenAI.getWrongFakeResponse());
        final UUID applicationId = uploadCV("src/test/resources/cv.pdf");
        applicationsProcessedProducer.publishApplicationReceivedEvent(applicationId, 7L);

        await()
            .pollInterval(Duration.ofSeconds(3))
            .atMost(30, SECONDS)
            .untilAsserted(() -> {
                assertTrue(applicationAnalysedEventConsumer.isApplicationAnalysisFailedEventPublished());

                final ApplicationAnalysisFailedEventDTO applicationAnalysisFailedEventDTO = applicationAnalysedEventConsumer.getApplicationAnalysisFailedEventDTO();
                assertNotNull(applicationAnalysisFailedEventDTO);
                assertEquals(applicationId.toString(), applicationAnalysisFailedEventDTO.getApplicationId());
                assertNotNull(applicationAnalysisFailedEventDTO.getMessage());
                assertNotNull(applicationAnalysisFailedEventDTO.getCode());
                assertEquals(AnalysingException.CODE_LLM_RESPONSE_PARSING_ERROR, applicationAnalysisFailedEventDTO.getCode());
            });
    }

    @Test
    void givenAWrongApplicationReceivedEvent_whenTheEventIsProcessed_ThenAnErrorEventIsPublishedToDLQ() throws Exception {
        final UUID applicationId = uploadCV("src/test/resources/cv.pdf");
        applicationsProcessedProducer.publishWrongApplicationReceivedEvent(applicationId, 7L);

        await()
            .pollInterval(Duration.ofSeconds(3))
            .atMost(30, SECONDS)
            .untilAsserted(() -> {
                assertTrue(applicationAnalysedEventConsumer.isApplicationAnalysisFailedEventPublished());

                final ApplicationAnalysisFailedEventDTO applicationAnalysisFailedEventDTO = applicationAnalysedEventConsumer.getApplicationAnalysisFailedEventDTO();
                assertNotNull(applicationAnalysisFailedEventDTO);
                assertEquals(applicationId.toString(), applicationAnalysisFailedEventDTO.getApplicationId());
                assertNotNull(applicationAnalysisFailedEventDTO.getMessage());
                assertNotNull(applicationAnalysisFailedEventDTO.getCode());
                assertEquals(ERROR_PROCESSING_APPLICATION_RECEIVED_EVENT, applicationAnalysisFailedEventDTO.getCode());
            });
    }

    @Test
    void givenAnApplicationReceivedEventWithACorruptedFileStored_whenTheEventIsProcessed_ThenAnErrorEventIsPublishedToDLQ() throws Exception {
        final UUID applicationId = uploadCV("src/test/resources/cv_corrupted.pdf");
        applicationsProcessedProducer.publishApplicationReceivedEvent(applicationId, 7L);

        await()
            .pollInterval(Duration.ofSeconds(3))
            .atMost(30, SECONDS)
            .untilAsserted(() -> {
                assertTrue(applicationAnalysedEventConsumer.isApplicationAnalysisFailedEventPublished());

                final ApplicationAnalysisFailedEventDTO applicationAnalysisFailedEventDTO = applicationAnalysedEventConsumer.getApplicationAnalysisFailedEventDTO();
                assertNotNull(applicationAnalysisFailedEventDTO);
                assertEquals(applicationId.toString(), applicationAnalysisFailedEventDTO.getApplicationId());
                assertNotNull(applicationAnalysisFailedEventDTO.getMessage());
                assertNotNull(applicationAnalysisFailedEventDTO.getCode());
                assertEquals(TextExtractingException.CODE_TEXT_EXTRACT_ERROR, applicationAnalysisFailedEventDTO.getCode());
            });
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
