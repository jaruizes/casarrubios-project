package com.jaruiz.casarrubios.recruiters.services.globalposition.util.kafka;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class EventsProducer {

    @Inject
    @Channel("applications-scored-out") Emitter<String> applicationsScoredEmitter;

    @Inject
    @Channel("applications-received-out") Emitter<String> applicationsReceivedEmitter;

    @Inject
    @Channel("new-positions-published-out") Emitter<String> newPositionsPublishedEmitter;


    public void publishNewPosition() throws IOException {
        String event = readEventFile("events/recruiters.new-positions-published.txt");
        this.newPositionsPublishedEmitter.send(event);
    }

    public void publishApplicationReceived() throws IOException {
        String event = readEventFile("events/recruitment.applications-received.txt");
        this.applicationsReceivedEmitter.send(event);
    }

    public void publishApplicationScored() throws IOException {
        String event = readEventFile("events/recruitment.applications-scored.txt");
        this.applicationsScoredEmitter.send(event);
    }

    private static String readEventFile(String fileName) throws IOException {
        return new String(Objects.requireNonNull(EventsProducer.class.getClassLoader().getResourceAsStream(fileName)).readAllBytes(), StandardCharsets.UTF_8);
    }

}
