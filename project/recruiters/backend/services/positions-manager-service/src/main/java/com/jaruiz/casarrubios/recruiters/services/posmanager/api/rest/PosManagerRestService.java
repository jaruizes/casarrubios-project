package com.jaruiz.casarrubios.recruiters.services.posmanager.api.rest;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.jaruiz.casarrubios.recruiters.services.posmanager.api.rest.dto.*;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.PositionManagerService;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionInvalidException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.Benefit;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.Position;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.Requirement;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.Task;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PosManagerRestService implements PositionsApi {
    private static final Logger logger = Logger.getLogger(PosManagerRestService.class);

    private final PositionManagerService positionService;

    public PosManagerRestService(PositionManagerService positionService) {
        this.positionService = positionService;
    }

    @Override public List<PositionDTO> getAllPositions() {
        logger.info("Getting all positions");
        return positionService.getAllPositions().stream()
            .map(this::mapToPositionDTO)
            .toList();
    }

    @Override public PositionDetailDTO getPositionDetail(Long positionId) {
        logger.info("Getting position detail with Id: " + positionId);
        final Position position = positionService.getPositionDetail(positionId);
        logger.info("Position with Id: " + positionId + " found");

        return mapToPositionDetailDTO(position);
    }

    @Override public PositionDetailDTO updatePosition(Long positionId, PositionDetailDTO positionDetailDTO) {
        return null;
        /*try {
            return mapToPositionDetailDTO(positionService.updatePosition(mapToPosition(data)));
        } catch (PositionInvalidException | PositionNotFoundException e) {
            throw new RuntimeException(e);
        }*/
    }


    @Override public PositionDetailDTO createPosition(NewPositionDataDTO data) {
        return null;
        /*try {
            final Position positionCreated = positionService.createPosition(PosManagerRestService.mapToPosition(data));
            return PosManagerRestService.mapToPositionDetailDTO(positionCreated);
        } catch (PositionInvalidException e) {
            throw new RuntimeException(e);
        }*/
    }

    private PositionDTO mapToPositionDTO(Position position) {
        final PositionDTO positionDTO = new PositionDTO();
        positionDTO.setId(position.getId());
        positionDTO.setTitle(position.getTitle());
        positionDTO.setDescription(position.getDescription());
        positionDTO.setStatus(position.getStatus().ordinal());

        return positionDTO;
    }

    private static PositionDetailDTO mapToPositionDetailDTO(Position position) {
        final PositionDetailDTO positionDetailDTO = new PositionDetailDTO();
        positionDetailDTO.setId(position.getId());
        positionDetailDTO.setTitle(position.getTitle());
        positionDetailDTO.setDescription(position.getDescription());
        positionDetailDTO.setStatus(position.getStatus().ordinal());
        positionDetailDTO.setCreatedAt(Date.from(position.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        positionDetailDTO.setPublishedAt(Date.from(position.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));

        if (position.getRequirements() != null) {
            positionDetailDTO.setRequirements(position.getRequirements().stream()
                .map(PosManagerRestService::mapToRequirementDTO)
                .toList());
        }

        if (position.getBenefits() != null) {
            positionDetailDTO.setBenefits(position.getBenefits().stream()
                .map(PosManagerRestService::mapToBenefitDTO)
                .toList());
        }

        if (position.getTasks() != null) {
            positionDetailDTO.setTasks(position.getTasks().stream()
                .map(PosManagerRestService::mapToTaskDTO)
                .toList());
        }

        return positionDetailDTO;
    }

    private static RequirementDTO mapToRequirementDTO(Requirement requirement) {
        final RequirementDTO requirementDTO = new RequirementDTO();
        requirementDTO.setDescription(requirement.getDescription());
        requirementDTO.setKey(requirement.getKey());
        requirementDTO.setIsMandatory(requirement.isMandatory());
        requirementDTO.setValue(requirement.getValue());

        return requirementDTO;
    }

    private static BenefitDTO mapToBenefitDTO(Benefit benefit) {
        final BenefitDTO conditionDTO = new BenefitDTO();
        conditionDTO.setDescription(benefit.getDescription());

        return conditionDTO;
    }

    private static TaskDTO mapToTaskDTO(Task task) {
        final TaskDTO taskDTO = new TaskDTO();
        taskDTO.setDescription(task.getDescription());

        return taskDTO;
    }

    /*

    private static Position mapToPosition(PositionDetailDTO positionDetailDTO) {
        final List<Requirement> requirements = positionDetailDTO.getRequirements().stream()
            .map(PosManagerRestService::mapToRequirement)
            .toList();

        final List<Benefit> benefits = positionDetailDTO.getConditions().stream()
                                                        .map(PosManagerRestService::mapToCondition)
                                                        .toList();

        return new Position(
            positionDetailDTO.getId(),
            positionDetailDTO.getTitle(),
            positionDetailDTO.getDescription(),
            requirements, benefits);
    }

    private static Requirement mapToRequirement(RequirementDTO requirementDTO) {
        return new Requirement(requirementDTO.getDescription());
    }

    private static Benefit mapToCondition(ConditionDTO conditionDTO) {
        return new Benefit(conditionDTO.getDescription());
    }*/
}
