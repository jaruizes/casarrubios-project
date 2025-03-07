package com.jaruiz.casarrubios.recruiters.services.util.kafka;

import java.util.UUID;

import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

public class ApplicationsProducer {
    public static final String APPLICATION_NAME = "John Doe";
    public static final String APPLICATION_EMAIL = "email@email.com";
    public static final String APPLICATION_PHONE = "123456789";
    public static final String APPLICATION_CV = "1/0a3f2ff3-e169-4ef9-88b6-3f89635ca40a";
    public static final int APPLICATION_POSITION_ID = 1;
    public static final long APPLICATION_CREATED_AT = 1741104560112272L;


    @Inject
    @Channel("cdc-applications-out") Emitter<String> emitter;

    public UUID publishApplication() {
        final UUID applicationId = UUID.randomUUID();
        emitter.send(buildApplicationFake(applicationId));

        return applicationId;
    }

    public UUID publishIncompleteApplication() {
        final UUID applicationId = UUID.randomUUID();
        emitter.send(buildWrongApplicationFake(applicationId));

        return applicationId;
    }

    private String buildApplicationFake(UUID applicationId) {
        return "{\n" +
            "    \"id\": \"" + applicationId + "\",\n" +
            "    \"name\": \"" + APPLICATION_NAME + "\",\n" +
            "    \"email\": \"" + APPLICATION_EMAIL + "\",\n" +
            "    \"phone\": \"" + APPLICATION_PHONE + "\",\n" +
            "    \"cv\": \"" + APPLICATION_CV + "\",\n" +
            "    \"position_id\": " + APPLICATION_POSITION_ID + ",\n" +
            "    \"created_at\": " + APPLICATION_CREATED_AT + "\n" +
            "}";
    }

    private String buildWrongApplicationFake(UUID applicationId) {
        return "{\n" +
            "    \"id\": \"" + applicationId + "\",\n" +
            "    \"email\": \"" + APPLICATION_EMAIL + "\",\n" +
            "    \"cv\": \"" + APPLICATION_CV + "\",\n" +
            "    \"position_id\": " + APPLICATION_POSITION_ID + ",\n" +
            "    \"created_at\": " + APPLICATION_CREATED_AT + "\n" +
            "}";
    }
}
