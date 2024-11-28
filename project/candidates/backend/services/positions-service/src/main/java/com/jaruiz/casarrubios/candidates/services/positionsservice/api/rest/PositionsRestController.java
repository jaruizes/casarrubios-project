package com.jaruiz.casarrubios.candidates.services.positionsservice.api.rest;

import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positionsservice.api.rest.dto.ConditionDTO;
import com.jaruiz.casarrubios.candidates.services.positionsservice.api.rest.dto.PositionDTO;
import com.jaruiz.casarrubios.candidates.services.positionsservice.api.rest.dto.RequirementDTO;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.model.Condition;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.model.Requirement;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.ports.PositionServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/positions", produces = "application/json")
public class PositionsRestController {
    private static final Logger logger = LoggerFactory.getLogger(PositionsRestController.class);
    private final PositionServicePort positionService;

    public PositionsRestController(PositionServicePort positionService) {
        this.positionService = positionService;
    }

    @GetMapping(path = "/{positionId}")
    public ResponseEntity<PositionDTO> getPositionDetail(@PathVariable("positionId") long positionId) {
        final PositionDTO positionDTO;

        try {
            var position = this.positionService.getPositionDetail(positionId);
            positionDTO = new PositionDTO(position.getId(),
                position.getTitle(),
                position.getDescription(),
                position.getRequirements().stream().map(this::requirementsToRequirementsDTO).toList(),
                position.getConditions().stream().map(this::conditionsToConditionsDTO).toList());


        } catch (PositionNotFoundException e) {
            logger.error("Position with id {} not found. Returning 404 status", positionId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        logger.info("Position with id {} found. Returning 200 status", positionId);
        return new ResponseEntity<>(positionDTO, HttpStatus.OK);
    }

    @GetMapping
    public List<PositionDTO> getAllPositions() {
        return this.positionService.getPositions().stream().map(position -> new PositionDTO(position.getId(),
            position.getTitle(),
            position.getDescription(),
            null,
            null)).toList();
    }

    private RequirementDTO requirementsToRequirementsDTO(Requirement requirement) {
        return new RequirementDTO(requirement.getId(), requirement.getDescription());
    }

    private ConditionDTO conditionsToConditionsDTO(Condition condition) {
        return new ConditionDTO(condition.getId(), condition.getDescription());
    }


}
