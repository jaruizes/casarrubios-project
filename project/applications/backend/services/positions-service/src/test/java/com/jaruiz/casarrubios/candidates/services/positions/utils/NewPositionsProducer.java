package com.jaruiz.casarrubios.candidates.services.positions.utils;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.jaruiz.casarrubios.candidates.services.positions.infrastructure.Config;
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
public class NewPositionsProducer {
    private static final Logger logger = LoggerFactory.getLogger(NewPositionsProducer.class);
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

    private int requirementsIndex = 0;
    private int benefitsIndex = 0;
    private int tasksIndex = 0;

    public void publishPositionPublishedEvent(long positionId) {
        publishApplicationReceivedEvent(positionId, buildNewPositionEvent(positionId));
    }

    private void publishApplicationReceivedEvent(long positionId, String positionPublishedEvent) {
        waitTillListenersReady();
        try {
            SendResult<String, Object> result = kafkaTemplate.send(config.getNewPositionsPublishedTopic(), String.valueOf(positionId), positionPublishedEvent).get();
            if (result.getRecordMetadata() == null) {
                fail("Error publishing application received event");
            }
        } catch (Exception e) {
            fail("Error publishing application processed event");
        }
    }

    private void waitTillListenersReady() {
        await().atMost(10, TimeUnit.SECONDS)
               .until(() -> kafkaListenerEndpointRegistry.getAllListenerContainers().stream().toList().getFirst().isRunning());
    }

    private String buildNewPositionEvent(long positionId) {
        return "{\n" + 
            "  \"id\": " + positionId + ",\n" +
            "  \"title\": \"Title\",\n" + 
            "  \"description\": \"Description\",\n" + 
            "  \"status\": \"OPEN\",\n" + 
            "  \"createdAt\": 1742398961605,\n" + 
            "  \"publishedAt\": 1742398961605,\n" + 
            "  \"tags\": \"Tag1, Tag2\",\n" + 
            "  \"requirements\": [\n" + addRequirement(positionId) + "  ],\n" +
            "  \"benefits\": [\n" + addBenefit(positionId) + "  ],\n" +
            "  \"tasks\": [\n" + addTask(positionId) + "  ]\n" +
            "}";
    }

    private String addRequirement(long positionId) {
        return "    {\n" +
            "      \"id\": " + requirementsIndex++ +",\n" +
            "      \"position_id\": " + positionId + ",\n" +
            "      \"key\": \"Key\",\n" +
            "      \"value\": \"Value\",\n" +
            "      \"description\": \"Description\",\n" +
            "      \"mandatory\": true\n" +
            "    }\n";
    }

    private String addTask(long positionId) {
        return "    {\n" +
            "      \"id\": " + tasksIndex++ +",\n" +
            "      \"position_id\": " + positionId + ",\n" +
            "      \"description\": \"Description\"\n" +
            "    }\n";
    }

    private String addBenefit(long positionId) {
        return "    {\n" +
            "      \"id\": " + benefitsIndex++ +",\n" +
            "      \"position_id\": " + positionId + ",\n" +
            "      \"description\": \"Description\"\n" +
            "    }\n";
    }
}
