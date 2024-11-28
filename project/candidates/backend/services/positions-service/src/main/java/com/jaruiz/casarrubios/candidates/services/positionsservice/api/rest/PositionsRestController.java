package com.jaruiz.casarrubios.candidates.services.positionsservice.api.rest;

import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positionsservice.api.rest.dto.ConditionDTO;
import com.jaruiz.casarrubios.candidates.services.positionsservice.api.rest.dto.PositionDTO;
import com.jaruiz.casarrubios.candidates.services.positionsservice.api.rest.dto.PositionDetailDTO;
import com.jaruiz.casarrubios.candidates.services.positionsservice.api.rest.dto.RequirementDTO;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.model.Position;
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
public class PositionsRestController implements PositionsApi {
    private static final Logger logger = LoggerFactory.getLogger(PositionsRestController.class);
    private final PositionServicePort positionService;

    public PositionsRestController(PositionServicePort positionService) {
        this.positionService = positionService;
    }

    @GetMapping(path = "/{positionId}")
    public ResponseEntity<PositionDetailDTO> getPositionDetail(@PathVariable("positionId") Long positionId) {
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

    @GetMapping
    public ResponseEntity<List<PositionDTO>> getAllPositions() {
        logger.info("GET received: getting all positions");
        final List<PositionDTO> positionDTOS = this.positionService.getPositions().stream().map(this::positionToPositionDTO).toList();

        logger.info("GET response: returning {} positions", positionDTOS.size());
        return new ResponseEntity<>(positionDTOS, HttpStatus.OK);
    }


    private PositionDTO positionToPositionDTO(Position position) {
        final PositionDTO positionDTO = new PositionDTO();
        positionDTO.setId(position.getId());
        positionDTO.setTitle(position.getTitle());
        positionDTO.setDescription(position.getDescription());

        return positionDTO;
    }

    private static PositionDetailDTO positionToPositionDetailDTO(Position position) {
        final PositionDetailDTO positionDetailDTO = new PositionDetailDTO();
        positionDetailDTO.setId(position.getId());
        positionDetailDTO.setTitle(position.getTitle());
        positionDetailDTO.setDescription(position.getDescription());
        positionDetailDTO.setRequirements(position.getRequirements().stream().map(requirement -> {
            var requirementDTO = new RequirementDTO();
            requirementDTO.setDescription(requirement.getDescription());
            return requirementDTO;
        }).toList());
        positionDetailDTO.setConditions(position.getConditions().stream().map(condition -> {
            var conditionDTO = new ConditionDTO();
            conditionDTO.setDescription(condition.getDescription());
            return conditionDTO;
        }).toList());
        return positionDetailDTO;
    }


}
