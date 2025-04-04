package com.jaruiz.casarrubios.recruiters.services.applications.api.input.async;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaruiz.casarrubios.recruiters.services.applications.api.input.async.dto.NewApplicationReceivedDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.api.input.async.mappers.ApplicationMapper;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ApplicationsAnalyzerService;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Application;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.ApplicationAnalyzerEventsPublisherPort;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ApplicationsAnalyzerAsyncAPI {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationsAnalyzerAsyncAPI.class);
    public static final String ERROR_PROCESSING_APPLICATION_RECEIVED_EVENT = "ARE0001";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApplicationMapper mapper;
    private final ApplicationsAnalyzerService applicationsAnalyzerService;
    private final ApplicationAnalyzerEventsPublisherPort eventPublisher;


    public ApplicationsAnalyzerAsyncAPI(ApplicationMapper mapper, ApplicationsAnalyzerService applicationsAnalyzerService,
        ApplicationAnalyzerEventsPublisherPort eventPublisher) {
        this.mapper = mapper;
        this.applicationsAnalyzerService = applicationsAnalyzerService;
        this.eventPublisher = eventPublisher;
    }

    @KafkaListener(id = "application-received-listener", topics = "#{config.applicationsReceivedTopic}", groupId = "analysing-services")
    @WithSpan("applications-received-event-handler")
    public void handleApplicationReceivedEvent(ConsumerRecord<String, String> record) {
        try {
            NewApplicationReceivedDTO application = objectMapper.readValue(record.value(), NewApplicationReceivedDTO.class);
            logger.info("Application received [key = {}, value = {}]", record.key(), application);

            final Application newApplication = mapper.newApplicationReceivedToApplication(application);
            this.applicationsAnalyzerService.analyzeApplication(newApplication);
            logger.info("Application analyzed [application id = {}]", newApplication.getId());

        } catch (JsonProcessingException e) {
            logger.error("Error processing application received [key = {}, value = {}]", record.key(), record.value());
            String message = "[key: " + record.key() + "] [value: " + record.value() + "]";
            this.eventPublisher.sendToDQL(UUID.fromString(record.key()), ERROR_PROCESSING_APPLICATION_RECEIVED_EVENT, message);
        }
    }
}
