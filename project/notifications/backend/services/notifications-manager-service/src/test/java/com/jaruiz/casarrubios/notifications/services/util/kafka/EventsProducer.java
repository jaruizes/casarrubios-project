package com.jaruiz.casarrubios.notifications.services.util.kafka;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class EventsProducer {

    @Inject
    @Channel("applications-scored-out") Emitter<String> applicationsScoredEmitter;

    public void publishApplicationGoodScored() throws IOException {
        String event = readEventFile("events/recruitment.applications-good-scored.txt");
        this.send(event);
    }

    public void publishApplicationBadScored() throws IOException {
        String event = readEventFile("events/recruitment.applications-bad-scored.txt");
        this.send(event);
    }

    private void send(String event) throws IOException {
        this.applicationsScoredEmitter.send(event);
    }

    private static String readEventFile(String fileName) throws IOException {
        return new String(Objects.requireNonNull(EventsProducer.class.getClassLoader().getResourceAsStream(fileName)).readAllBytes(), StandardCharsets.UTF_8);
    }

}
