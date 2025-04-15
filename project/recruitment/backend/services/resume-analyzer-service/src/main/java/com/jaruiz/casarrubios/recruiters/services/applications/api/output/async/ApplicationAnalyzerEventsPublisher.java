package com.jaruiz.casarrubios.recruiters.services.applications.api.output.async;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaruiz.casarrubios.recruiters.services.applications.api.output.async.dto.ApplicationAnalysedEventDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.api.output.async.dto.ApplicationAnalysisFailedEventDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.api.output.async.dto.ResumeAnalysisDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.api.output.async.mappers.ApplicationAnalysisMapper;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.ResumeAnalysis;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.ApplicationAnalyzerEventsPublisherPort;
import com.jaruiz.casarrubios.recruiters.services.applications.infrastructure.Config;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class ApplicationAnalyzerEventsPublisher implements ApplicationAnalyzerEventsPublisherPort {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationAnalyzerEventsPublisher.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ApplicationAnalysisMapper mapper;
    private final Config config;

    public ApplicationAnalyzerEventsPublisher(KafkaTemplate<String, String> kafkaTemplate, ApplicationAnalysisMapper mapper, Config config) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
        this.config = config;
    }

    @Override
    @WithSpan("publishing-application-analysed-event")
    public void sendApplicationAnalysedEvent(UUID applicationId, long positionId, UUID candidateId, ResumeAnalysis resumeAnalysis) {
        logger.info("Sending application analysed event [cvAnalysis = {}]", resumeAnalysis);
        final ApplicationAnalysedEventDTO applicationAnalysedEventDTO = buildApplicationAnalysedEvent(applicationId, positionId, candidateId, resumeAnalysis);
        final String applicationAnalysedEvent = applicationAnalysedEventToJsonString(applicationAnalysedEventDTO);

        final String applicationsAnalyzedTopic = this.config.getApplicationsAnalyzedTopic();
        final ProducerRecord<String, String> applicationAnalysedEventRecord = new ProducerRecord<>(applicationsAnalyzedTopic, applicationId.toString(), applicationAnalysedEvent);
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

    @Override
    public void sendToDQL(UUID applicationId, String code, String message) {
        logger.info("Sending application to DQL [applicationId = {}, message = {}]", applicationId, message);
        final ApplicationAnalysisFailedEventDTO applicationAnalysisFailedEventDTO = ApplicationAnalysisFailedEventDTO.builder()
                                                                                                                     .applicationId(applicationId.toString())
                                                                                                                     .message(message)
                                                                                                                     .code(code)
                                                                                                                     .build();
        final String applicacionAnalysisFailedEventJsonString = applicationAnalysedFailedEventToJsonString(applicationAnalysisFailedEventDTO);
        final String applicationsDLQtopic = this.config.getDlqTopic();
        final ProducerRecord<String, String> applicationToDQLRecord = new ProducerRecord<>(applicationsDLQtopic, applicationId.toString(), applicacionAnalysisFailedEventJsonString);
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

    private String applicationAnalysedFailedEventToJsonString(ApplicationAnalysisFailedEventDTO applicationAnalysedFailedEventDTO) {
        try {
            return objectMapper.writeValueAsString(applicationAnalysedFailedEventDTO);
        } catch (JsonProcessingException e) {
            logger.error("Error building event to DQL topic [applicationId = {}]", applicationAnalysedFailedEventDTO.getApplicationId());
            logger.error(e.getMessage());
        }

        return null;
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

    private ApplicationAnalysedEventDTO buildApplicationAnalysedEvent(UUID applicationId, long positionId, UUID candidateId, ResumeAnalysis resumeAnalysis) {
        final ResumeAnalysisDTO resumeAnalysisDTO = this.mapper.cvAnalysisToResumeAnalysisDTO(resumeAnalysis);
        return ApplicationAnalysedEventDTO.builder()
                                          .applicationId(applicationId)
                                          .positionId(positionId)
                                          .analysis(resumeAnalysisDTO)
                                          .candidateId(candidateId)
                                          .build();
    }
}
