package com.jaruiz.casarrubios.recruiters.services.applications;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.jaruiz.casarrubios.recruiters.services.applications.api.async.ApplicationsProcessedAsyncAPI;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Score;
import com.jaruiz.casarrubios.recruiters.services.applications.util.kafka.ApplicationsProcessedProducer;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ScoringServiceApplicationTests {

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
        registry.add("cv.bucket.name", ScoringServiceApplicationTests::getBucketName);
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired ApplicationsProcessedProducer applicationsProcessedProducer;
    @Autowired ApplicationsProcessedAsyncAPI applicationsProcessedAsyncAPI;
    @Autowired MinioClient minioClient;

    @BeforeAll
    static void beforeAll() {
        kafka.start();
        postgres.start();
        minio.start();
    }

    @AfterAll
    static void afterAll() {
        kafka.stop();
        postgres.stop();
        minio.stop();
    }

    @Test
    void givenAPositionAndAnApplication_whenTheApplicationIsSuitable_ThenAScoreGreaterThan60isReceived() throws Exception {
        final UUID applicationId = uploadCV();
        applicationsProcessedProducer.publishApplicationProcessedEvent(applicationId, 1L);

        Awaitility.await()
                  .atMost(120, TimeUnit.SECONDS)
                  .until(() -> applicationsProcessedAsyncAPI.isScoreCalculated());

        final Score score = applicationsProcessedAsyncAPI.getScore();
        Assertions.assertNotNull(score);
        Assertions.assertTrue(score.getScore() >= 60);
        Assertions.assertTrue(score.getEmbeddingScore() >= 0);
        Assertions.assertTrue(score.getRequirementsMatch() >= 0);
        Assertions.assertTrue(score.getTagSimilarity() >= 0);

    }

    @Test
    void givenAPositionAndAnApplication_whenTheApplicationIsNotSuitable_ThenAScoreLessThan60isReceived() throws Exception {
        final UUID applicationId = uploadCV();
        applicationsProcessedProducer.publishApplicationProcessedEvent(applicationId, 5L);

        Awaitility.await()
                  .atMost(120, TimeUnit.SECONDS)
                  .until(() -> applicationsProcessedAsyncAPI.isScoreCalculated());

        final Score score = applicationsProcessedAsyncAPI.getScore();
        Assertions.assertNotNull(score);
        Assertions.assertTrue(score.getScore() < 60);
        Assertions.assertTrue(score.getEmbeddingScore() >= 0);
        Assertions.assertTrue(score.getRequirementsMatch() >= 0);
        Assertions.assertTrue(score.getTagSimilarity() >= 0);

    }

    private UUID uploadCV() throws Exception {
        final UUID applicationId = UUID.randomUUID();
        FileInputStream inputStream = new FileInputStream("src/test/resources/cv.pdf");

        this.minioClient.makeBucket(MakeBucketArgs.builder().bucket(getBucketName()).build());
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
