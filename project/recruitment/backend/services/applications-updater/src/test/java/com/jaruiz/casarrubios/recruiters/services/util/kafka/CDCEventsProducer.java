package com.jaruiz.casarrubios.recruiters.services.util.kafka;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class CDCEventsProducer {
    public static final String APPLICATION_NAME = "John Doe";
    public static final String APPLICATION_EMAIL = "email@email.com";
    public static final String APPLICATION_PHONE = "123456789";
    public static final String APPLICATION_CV = "1/0a3f2ff3-e169-4ef9-88b6-3f89635ca40a";
    public static final int APPLICATION_POSITION_ID = 1;
    public static final long APPLICATION_CREATED_AT = 1742209886226900L;


    @Inject
    @Channel("cdc-applications-out") Emitter<String> emitter;

    public UUID publishChangeEvent() {
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
            "\t\"id\": \"" + applicationId + "\",\n" +
            "\t\"name\": \"" + APPLICATION_NAME + "\",\n" +
            "\t\"email\": \"" + APPLICATION_EMAIL + "\",\n" +
            "\t\"phone\": \"" + APPLICATION_PHONE + "\",\n" +
            "\t\"cv\": \"" + APPLICATION_CV  + "\",\n" +
            "\t\"position_id\": " + APPLICATION_POSITION_ID + ",\n" +
            "\t\"created_at\": " + APPLICATION_CREATED_AT + "\n" + "}";
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
