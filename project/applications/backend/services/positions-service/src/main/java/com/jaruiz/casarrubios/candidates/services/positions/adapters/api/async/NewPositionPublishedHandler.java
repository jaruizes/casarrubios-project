package com.jaruiz.casarrubios.candidates.services.positions.adapters.api.async;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.async.dto.NewPositionDTO;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.async.dto.PositionBenefitDTO;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.async.dto.PositionRequirementDTO;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.async.dto.PositionTaskDTO;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Benefits;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Position;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Requirement;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Task;
import com.jaruiz.casarrubios.candidates.services.positions.business.ports.PositionServicePort;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class NewPositionPublishedHandler {
    private static final Logger logger = LoggerFactory.getLogger(NewPositionPublishedHandler.class);
    private final ObjectMapper objectMapper;
    private final PositionServicePort positionService;

    public NewPositionPublishedHandler(PositionServicePort positionService) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        this.positionService = positionService;
    }

    @KafkaListener(id = "application-received-listener", topics = "#{config.newPositionsPublishedTopic}", groupId = "position-service", concurrency = "1")
    public void newPositionPublished(ConsumerRecord<Long, String> record) {
        try {
            logger.info("New position received");
            NewPositionDTO newPositionDTO = objectMapper.readValue(record.value(), NewPositionDTO.class);
            Position position = newPositionDTOToPosition(newPositionDTO);
            positionService.savePosition(position);
            logger.info("New position saved [positionId = {}]", position.getId());
        } catch (Exception e) {
            logger.error("Error processing new position [key = {}, value = {}]", record.key(), record.value());
            logger.error(e.getMessage());
        }

    }

    private Position newPositionDTOToPosition(NewPositionDTO newPositionDTO) {
        List<Requirement> requirements = newPositionDTO.getRequirements().stream().map(this::newRequirementDTOToRequirement).toList();
        List<Task> tasks = newPositionDTO.getTasks().stream().map(this::newTaskDTOToTask).toList();
        List<Benefits> benefits = newPositionDTO.getBenefits().stream().map(this::newConditionDTOToCondition).toList();

        return new Position(newPositionDTO.getId(),
            newPositionDTO.getTitle(),
            newPositionDTO.getDescription(),
            newPositionDTO.getTags(),
            getLocalDateTime(newPositionDTO.getCreatedAt()),
            requirements, benefits,
            tasks);
    }

    private Requirement newRequirementDTOToRequirement(PositionRequirementDTO requirementDTO) {
        return new Requirement(requirementDTO.getId(), requirementDTO.getKey(), requirementDTO.getValue(), requirementDTO.getDescription(), requirementDTO.isMandatory());
    }

    private Task newTaskDTOToTask(PositionTaskDTO positionTaskDTO) {
        return new Task(positionTaskDTO.getId(), positionTaskDTO.getDescription());
    }

    private Benefits newConditionDTOToCondition(PositionBenefitDTO positionBenefitDTO) {
        return new Benefits(positionBenefitDTO.getId(), positionBenefitDTO.getDescription());
    }

    private LocalDateTime getLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());
    }

}
