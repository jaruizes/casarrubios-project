package com.jaruiz.casarrubios.recruiters.services.applications.util.kafka;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.jaruiz.casarrubios.recruiters.services.applications.infrastructure.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import static org.junit.Assert.fail;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Service
public class ApplicationsProcessedProducer {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationsProcessedProducer.class);
    public static final String APPLICATION_NAME = "John Doe";
    public static final String APPLICATION_EMAIL = "email@email.com";
    public static final String APPLICATION_PHONE = "123456789";
    public static final String APPLICATION_CV = "1/0a3f2ff3-e169-4ef9-88b6-3f89635ca40a";
    public static final long APPLICATION_CREATED_AT = 1741104560112272L;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private Config config;
    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    public void publishWrongApplicationReceivedEvent(UUID applicationId, long positionId) {
        publishApplicationReceivedEvent(applicationId, buildWrongApplicationReceivedEvent(applicationId, positionId));
    }

    public void publishApplicationReceivedEvent(UUID applicationId, long positionId) {
        publishApplicationReceivedEvent(applicationId, buildApplicationReceivedEvent(applicationId, positionId));
    }

    private void publishApplicationReceivedEvent(UUID applicationId, String applicationReceivedEvent) {
        waitTillListenersReady();
        try {
            SendResult<String, Object> result = kafkaTemplate.send(config.getApplicationsReceivedTopic(), applicationId.toString(), applicationReceivedEvent).get();
            if (result.getRecordMetadata() == null) {
                fail("Error publishing application received event");
            }
        } catch (Exception e) {
            fail("Error publishing application processed event");
        }
    }

    private void waitTillListenersReady() {
        await().atMost(10, TimeUnit.SECONDS)
               .until(() -> Objects.requireNonNull(kafkaListenerEndpointRegistry.getListenerContainer("application-received-listener")).isRunning() &&
                   Objects.requireNonNull(kafkaListenerEndpointRegistry.getListenerContainer("application-analysed-listener")).isRunning() &&
                   Objects.requireNonNull(kafkaListenerEndpointRegistry.getListenerContainer("application-dlq-listener")).isRunning());
    }

    private String buildApplicationReceivedEvent(UUID applicationId, long positionId) {
        return "{\n" +
            "    \"id\": \"" + applicationId + "\",\n" +
            "    \"name\": \"" + APPLICATION_NAME + "\",\n" +
            "    \"email\": \"" + APPLICATION_EMAIL + "\",\n" +
            "    \"phone\": \"" + APPLICATION_PHONE + "\",\n" +
            "    \"cv\": \"" + APPLICATION_CV + "\",\n" +
            "    \"position_id\": " + positionId + ",\n" +
            "    \"created_at\": " + APPLICATION_CREATED_AT + "\n" +
            "}";
    }

    private String buildWrongApplicationReceivedEvent(UUID applicationId, long positionId) {
        return "{\n" +
            "    \"id\": \"" + applicationId + "\",\n" +
            "    \"namdsdse\": \"" + APPLICATION_NAME + "\",\n" +
            "    \"emadsdsil\": \"" + APPLICATION_EMAIL + "\",\n" +
            "    \"phodsdsne\": \"" + APPLICATION_PHONE + "\",\n" +
            "    \"cv\": \"" + APPLICATION_CV + "\",\n" +
            "    \"position_id\": " + positionId + ",\n" +
            "    \"created_at\": " + APPLICATION_CREATED_AT + "\n" +
            "}";
    }
}
