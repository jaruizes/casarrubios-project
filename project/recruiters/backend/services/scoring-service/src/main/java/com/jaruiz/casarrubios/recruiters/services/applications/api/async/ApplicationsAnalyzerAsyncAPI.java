package com.jaruiz.casarrubios.recruiters.services.applications.api.async;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaruiz.casarrubios.recruiters.services.applications.api.async.dto.ApplicationAnalysedEventDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.api.async.dto.ApplicationAnalysisFailedEventDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.api.async.dto.NewApplicationReceivedDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.api.async.dto.ResumeAnalysisDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.api.async.mappers.ApplicationMapper;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ApplicationsAnalyzerService;
import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.AnalysingException;
import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.CVNotFoundException;
import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.TextExtractingException;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Application;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.ResumeAnalysis;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.ApplicationAnalysedProducerPort;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import static com.jaruiz.casarrubios.recruiters.services.applications.infrastructure.Config.*;

@Service
public class ApplicationsAnalyzerAsyncAPI implements ApplicationAnalysedProducerPort {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationsAnalyzerAsyncAPI.class);
    public static final String ERROR_RESUME_NOT_FOUND = "AA0002";
    public static final String ERROR_PROCESSING_APPLICATION_RECEIVED_EVENT = "ARE0001";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApplicationMapper mapper;
    private final ApplicationsAnalyzerService applicationsAnalyzerService;
    private final KafkaTemplate<String, String> kafkaTemplate;


    public ApplicationsAnalyzerAsyncAPI(ApplicationMapper mapper, ApplicationsAnalyzerService applicationsAnalyzerService,
        KafkaTemplate<String, String> kafkaTemplate) {
        this.mapper = mapper;
        this.applicationsAnalyzerService = applicationsAnalyzerService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(id = "application-received-listener", topics = APPLICATIONS_RECEIVED_TOPIC, groupId = "analysing-services")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            NewApplicationReceivedDTO application = objectMapper.readValue(record.value(), NewApplicationReceivedDTO.class);
            logger.info("Application received [key = {}, value = {}]", record.key(), application);

            final Application newApplication = mapper.newApplicationReceivedToApplication(application);
            final ResumeAnalysis analysis = this.applicationsAnalyzerService.analyzeApplication(newApplication);
            logger.info("Application analyzed [application id = {}]", newApplication.getId());

            this.sendApplicationAnalysedEvent(newApplication.getId(), analysis);
            logger.info("Application analysed event sent [application id = {}]", newApplication.getId());
        } catch (AnalysingException e) {
            this.sendToDQL(UUID.fromString(record.key()), e.getCode(), e.getMessage());
        } catch (CVNotFoundException e) {
            final String message = "Error processing application [key = " + record.key() + "]. Resume not found in storage";
            logger.error(message);
            this.sendToDQL(UUID.fromString(record.key()), ERROR_RESUME_NOT_FOUND, message);
        } catch (JsonProcessingException e) {
            logger.error("Error processing application received [key = {}, value = {}]", record.key(), record.value());
            String message = "[key: " + record.key() + "] [value: " + record.value() + "]";
            this.sendToDQL(UUID.fromString(record.key()), ERROR_PROCESSING_APPLICATION_RECEIVED_EVENT, message);
        } catch (TextExtractingException e) {
            this.sendToDQL(UUID.fromString(record.key()), e.getCode(), e.getMessage());
        }
    }

    @Override
    public void sendApplicationAnalysedEvent(UUID applicationId, ResumeAnalysis resumeAnalysis) {
        logger.info("Sending application analysed event [cvAnalysis = {}]", resumeAnalysis);
        final ApplicationAnalysedEventDTO applicationAnalysedEventDTO = buildApplicationAnalysedEvent(applicationId, resumeAnalysis);
        final String applicationAnalysedEvent = applicationAnalysedEventToJsonString(applicationAnalysedEventDTO);

        final ProducerRecord<String, String> applicationAnalysedEventRecord = new ProducerRecord<>(APPLICATIONS_ANALYSED_TOPIC, applicationId.toString(), applicationAnalysedEvent);
        CompletableFuture<SendResult<String, String>> result = this.kafkaTemplate.send(applicationAnalysedEventRecord);
        result.whenComplete((sendResult, throwable) -> {
            if (throwable != null) {
                logger.error("Error sending application analysed event [cvAnalysis = {}]", resumeAnalysis);
                logger.error(throwable.getMessage());
            } else {
                logger.info("Application analysed event sent [applicationId = {}]", applicationId);
            }
        });

    }

    private ApplicationAnalysedEventDTO buildApplicationAnalysedEvent(UUID applicationId, ResumeAnalysis resumeAnalysis) {
        final ResumeAnalysisDTO resumeAnalysisDTO = this.mapper.cvAnalysisToResumeAnalysisDTO(resumeAnalysis);
        return ApplicationAnalysedEventDTO.builder()
                                          .applicationId(applicationId)
                                          .analysis(resumeAnalysisDTO)
                                          .build();
    }

    private void sendToDQL(UUID applicationId, String code, String message) {
        logger.info("Sending application to DQL [applicationId = {}, message = {}]", applicationId, message);
        final ApplicationAnalysisFailedEventDTO applicationAnalysisFailedEventDTO = ApplicationAnalysisFailedEventDTO.builder()
                                                                                                                     .applicationId(applicationId.toString())
                                                                                                                     .message(message)
                                                                                                                     .code(code)
                                                                                                                     .build();
        final String applicacionAnalysisFailedEventJsonString = applicationAnalysedFailedEventToJsonString(applicationAnalysisFailedEventDTO);
        final ProducerRecord<String, String> applicationToDQLRecord = new ProducerRecord<>(APPLICATIONS_DQL_TOPIC, applicationId.toString(), applicacionAnalysisFailedEventJsonString);
        CompletableFuture<SendResult<String, String>> result = this.kafkaTemplate.send(applicationToDQLRecord);
        result.whenComplete((sendResult, throwable) -> {
            if (throwable != null) {
                logger.error("Error sending application to DQL [applicationId = {}, message = {}]", applicationId, message);
                logger.error(throwable.getMessage());
            } else {
                logger.info("Application sent to DQL [applicationId = {}, message = {}]", applicationId, message);
            }
        });
    }

    private String applicationAnalysedEventToJsonString(ApplicationAnalysedEventDTO applicationAnalysedEventDTO) {
        try {
            return objectMapper.writeValueAsString(applicationAnalysedEventDTO);
        } catch (JsonProcessingException e) {
            logger.error("Error processing application analysed event [applicationId = {}]", applicationAnalysedEventDTO.getApplicationId());
            logger.error(e.getMessage());
        }

        return null;
    }

    private String applicationAnalysedFailedEventToJsonString(ApplicationAnalysisFailedEventDTO applicationAnalysedFailedEventDTO) {
        try {
            return objectMapper.writeValueAsString(applicationAnalysedFailedEventDTO);
        } catch (JsonProcessingException e) {
            logger.error("Error building event to DQL topic [applicationId = {}]", applicationAnalysedFailedEventDTO.getApplicationId());
            logger.error(e.getMessage());
        }

        return null;
    }
}
