package com.jaruiz.casarrubios.recruiters.services.applications.util.kafka;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ApplicationsProcessedProducer {

    public static final String APPLICATION_NAME = "John Doe";
    public static final String APPLICATION_EMAIL = "email@email.com";
    public static final String APPLICATION_PHONE = "123456789";
    public static final String APPLICATION_CV = "1/0a3f2ff3-e169-4ef9-88b6-3f89635ca40a";
    public static final long APPLICATION_CREATED_AT = 1741104560112272L;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void publishApplicationProcessedEvent(UUID applicationId, long positionId) {
        kafkaTemplate.send("applications-processed", applicationId.toString(), buildApplicationFake(applicationId, positionId));
    }

    private String buildApplicationFake(UUID applicationId, long positionId) {
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
}
