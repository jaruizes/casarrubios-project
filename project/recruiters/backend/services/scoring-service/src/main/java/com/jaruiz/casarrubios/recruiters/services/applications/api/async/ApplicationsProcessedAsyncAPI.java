package com.jaruiz.casarrubios.recruiters.services.applications.api.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaruiz.casarrubios.recruiters.services.applications.api.async.dto.ApplicationDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ScoringService;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Application;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Score;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ApplicationsProcessedAsyncAPI {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationsProcessedAsyncAPI.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Score score;
    private final ScoringService scoringService;

    public ApplicationsProcessedAsyncAPI(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @KafkaListener(topics = "applications-processed", groupId = "scoring-service")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            ApplicationDTO application = objectMapper.readValue(record.value(), ApplicationDTO.class);
            logger.info("Application received [key = {}, value = {}]", record.key(), application);

            score = this.scoringService.evaluateApplication(toApplication(application));

            logger.info("\n\n\n------------------------------------------------------------------------");
            logger.info("Application scored [key = {}, score = {}]", record.key(), score);
            logger.info("------------------------------------------------------------------------\n\n\n");

        } catch (JsonProcessingException e) {
            logger.error("Error processing application [key = {}, value = {}]", record.key(), record.value());
            logger.error(e.getMessage());
        }
    }

    public boolean isScoreCalculated() {
        return score != null;
    }

    public Score getScore() {
        return score;
    }

    private Application toApplication(ApplicationDTO applicationDTO) {
        return new Application(applicationDTO.getId(),
            applicationDTO.getName(),
            applicationDTO.getEmail(),
            applicationDTO.getPhone(),
            applicationDTO.getCv(),
            applicationDTO.getPositionId(),
            applicationDTO.getCreatedAt());
    }
}
