package com.jaruiz.casarrubios.recruiters.services.applications.util.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaruiz.casarrubios.recruiters.services.applications.api.async.dto.ApplicationAnalysedEventDTO;
import com.jaruiz.casarrubios.recruiters.services.applications.api.async.dto.ApplicationAnalysisFailedEventDTO;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import static com.jaruiz.casarrubios.recruiters.services.applications.infrastructure.Config.APPLICATIONS_ANALYSED_TOPIC;
import static com.jaruiz.casarrubios.recruiters.services.applications.infrastructure.Config.APPLICATIONS_DQL_TOPIC;

@Service
public class ApplicationAnalysedEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationAnalysedEventConsumer.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Getter private ApplicationAnalysedEventDTO applicationAnalysedEventDTO;
    @Getter private ApplicationAnalysisFailedEventDTO applicationAnalysisFailedEventDTO;

    @KafkaListener(id = "application-analysed-listener", topics = APPLICATIONS_ANALYSED_TOPIC, groupId = "test-service")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            this.applicationAnalysedEventDTO = objectMapper.readValue(record.value(), ApplicationAnalysedEventDTO.class);
        } catch (JsonProcessingException e) {
            logger.error("Error processing application [key = {}, value = {}]", record.key(), record.value());
            logger.error(e.getMessage());
        }
    }

    @KafkaListener(id = "application-dlq-listener", topics = APPLICATIONS_DQL_TOPIC, groupId = "test-service")
    public void consumeDLQ(ConsumerRecord<String, String> record) {
        try {
            this.applicationAnalysisFailedEventDTO = objectMapper.readValue(record.value(), ApplicationAnalysisFailedEventDTO.class);
        } catch (JsonProcessingException e) {
            logger.error("Error processing application [key = {}, value = {}]", record.key(), record.value());
            logger.error(e.getMessage());
        }
    }

    public boolean isApplicationAnalysedEventPublished() {
        return applicationAnalysedEventDTO != null;
    }

    public boolean isApplicationAnalysisFailedEventPublished() {
        return applicationAnalysisFailedEventDTO != null;
    }

}
