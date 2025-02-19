package com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest.dto.*;
import com.jaruiz.casarrubios.candidates.services.positions.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Position;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.PositionsList;
import com.jaruiz.casarrubios.candidates.services.positions.business.ports.PositionServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PositionsRestController implements PositionsApi {
    private static final Logger logger = LoggerFactory.getLogger(PositionsRestController.class);
    private final PositionServicePort positionService;

    public PositionsRestController(PositionServicePort positionService) {
        this.positionService = positionService;
    }

    public ResponseEntity<PositionDetailDTO> getPositionDetail(Long positionId) {
        logger.info("GET received: getting position with id {}", positionId);

        if (positionId == null) {
            logger.error("GET: Position id is null. Returning 400 status");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        try {
            var position = this.positionService.getPositionDetail(positionId);
            final var positionDetailDTO = positionToPositionDetailDTO(position);

            logger.info("Position with id {} found. Returning 200 status", positionId);
            return new ResponseEntity<>(positionDetailDTO, HttpStatus.OK);

        } catch (PositionNotFoundException e) {
            logger.error("Position with id {} not found. Returning 404 status", positionId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<PaginatedPositionsDTO> getAllPositions(Integer page, Integer size) {
        logger.info("GET received: getting all positions");
        final PositionsList positionList = this.positionService.getPositions(page, size);
        final PaginatedPositionsDTO paginatedPositionsDTO = new PaginatedPositionsDTO();
        paginatedPositionsDTO.setNumber(page);
        paginatedPositionsDTO.setSize(size);
        paginatedPositionsDTO.setTotalElements(positionList.getTotal());
        paginatedPositionsDTO.setContent(positionList.getPositions().stream().map(this::positionToPositionDTO).toList());
        paginatedPositionsDTO.setTotalPages((int) Math.ceil((double) positionList.getTotal() / size));

        logger.info("GET response: returning page {} with {} positions", page, size);
        return new ResponseEntity<>(paginatedPositionsDTO, HttpStatus.OK);
    }


    private PositionDTO positionToPositionDTO(Position position) {
        final PositionDTO positionDTO = new PositionDTO();
        positionDTO.setId(position.getId());
        positionDTO.setTitle(position.getTitle());
        positionDTO.setDescription(position.getDescription());
        positionDTO.setTags(position.getTags());
        positionDTO.setCreatedAt(toIso8601(position.getCreatedAt()));
        positionDTO.setApplications(position.getApplications());


        return positionDTO;
    }

    private static PositionDetailDTO positionToPositionDetailDTO(Position position) {
        final PositionDetailDTO positionDetailDTO = new PositionDetailDTO();
        positionDetailDTO.setId(position.getId());
        positionDetailDTO.setTitle(position.getTitle());
        positionDetailDTO.setDescription(position.getDescription());
        positionDetailDTO.setTags(position.getTags());
        positionDetailDTO.setCreatedAt(toIso8601(position.getCreatedAt()));
        positionDetailDTO.setApplications(position.getApplications());

        positionDetailDTO.setRequirements(position.getRequirements().stream().map(requirement -> {
            var requirementDTO = new RequirementDTO();
            requirementDTO.setDescription(requirement.getDescription());
            requirementDTO.setKey(requirement.getKey());
            requirementDTO.setValue(requirement.getValue());
            requirementDTO.setMandatory(requirement.getMandatory());
            return requirementDTO;
        }).toList());

        positionDetailDTO.setConditions(position.getConditions().stream().map(condition -> {
            var conditionDTO = new ConditionDTO();
            conditionDTO.setDescription(condition.getDescription());
            return conditionDTO;
        }).toList());

        positionDetailDTO.setTasks(position.getTasks().stream().map(task -> {
            var taskDTO = new TaskDTO();
            taskDTO.setDescription(task.getDescription());
            return taskDTO;
        }).toList());

        return positionDetailDTO;
    }

    private static String toIso8601(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }


}
