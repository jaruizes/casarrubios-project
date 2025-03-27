package com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.kafka;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

@ApplicationScoped
public class CDCDataProducer {

    @Inject
    @Channel("cdc-recruiters-positions-out") Emitter<String> positionCDCEmitter;

    @Inject
    @Channel("cdc-recruiters-positions-requirements-out") Emitter<String> positionReqEmitter;

    @Inject
    @Channel("cdc-recruiters-positions-tasks-out") Emitter<String> positionTasksEmitter;

    @Inject
    @Channel("cdc-recruiters-positions-benefits-out") Emitter<String> positionBenefitEmitter;


    public void publishPosition(long positionId, boolean withReqs, boolean withTasks, boolean withBenefits) throws IOException {
        String event = readEventFile("events/position_cdc_message.txt")
            .replaceAll("<position_id>", String.valueOf(positionId));
        this.positionCDCEmitter.send(Message.of(event).addMetadata(OutgoingKafkaRecordMetadata.<Long>builder().withKey(1L)));
        if (withReqs) {
            this.publishRequirements(positionId);
        }

        if (withTasks) {
            this.publishTasks(positionId);
        }

        if (withBenefits) {
            this.publishBenefits(positionId);
        }
    }

    private void publishRequirements(long positionId) throws IOException {
        String events = readEventFile("events/position_reqs_cdc_message.txt")
            .replaceAll("<position_id>", String.valueOf(positionId));;
        List<String> reqsCDCMessages = List.of(events.split("#####"));
        reqsCDCMessages.forEach(event -> {
            this.positionReqEmitter.send(Message.of(event).addMetadata(OutgoingKafkaRecordMetadata.<Long>builder().withKey(1L)));
        });
    }

    private void publishTasks(long positionId) throws IOException {
        String events = readEventFile("events/position_tasks_cdc_message.txt")
            .replaceAll("<position_id>", String.valueOf(positionId));;
        List<String> reqsCDCMessages = List.of(events.split("#####"));
        reqsCDCMessages.forEach(event -> {
            this.positionTasksEmitter.send(Message.of(event).addMetadata(OutgoingKafkaRecordMetadata.<Long>builder().withKey(1L)));
        });
    }

    private void publishBenefits(long positionId) throws IOException {
        String events = readEventFile("events/position_benefits_cdc_message.txt")
            .replaceAll("<position_id>", String.valueOf(positionId));;
        List<String> reqsCDCMessages = List.of(events.split("#####"));
        reqsCDCMessages.forEach(event -> {
            this.positionBenefitEmitter.send(Message.of(event).addMetadata(OutgoingKafkaRecordMetadata.<Long>builder().withKey(1L)));
        });
    }

    private static String readEventFile(String fileName) throws IOException {
        return new String(Objects.requireNonNull(CDCDataProducer.class.getClassLoader().getResourceAsStream(fileName)).readAllBytes(), StandardCharsets.UTF_8);
    }

}
