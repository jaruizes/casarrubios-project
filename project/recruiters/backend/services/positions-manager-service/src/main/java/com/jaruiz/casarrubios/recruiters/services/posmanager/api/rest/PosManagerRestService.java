package com.jaruiz.casarrubios.recruiters.services.posmanager.api.rest;

import java.util.List;

import com.jaruiz.casarrubios.recruiters.services.posmanager.api.rest.beans.ConditionDTO;
import com.jaruiz.casarrubios.recruiters.services.posmanager.api.rest.beans.PositionDTO;
import com.jaruiz.casarrubios.recruiters.services.posmanager.api.rest.beans.PositionDetailDTO;
import com.jaruiz.casarrubios.recruiters.services.posmanager.api.rest.beans.RequirementDTO;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.PositionManagerService;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionInvalidException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.models.Condition;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.models.Position;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.models.Requirement;

public class PosManagerRestService implements PositionsResource {

    private final PositionManagerService positionService;

    public PosManagerRestService(PositionManagerService positionService) {
        this.positionService = positionService;
    }

    @Override public List<PositionDTO> getAllPositions() {
        return positionService.getAllPositions().stream()
            .map(this::mapToPositionDTO)
            .toList();
    }

    @Override public PositionDetailDTO createPosition(PositionDetailDTO data) {
        try {
            final Position positionCreated = positionService.createPosition(PosManagerRestService.mapToPosition(data));
            return PosManagerRestService.mapToPositionDetailDTO(positionCreated);
        } catch (PositionInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public PositionDetailDTO getPositionDetail(long positionId) {
        try {
            return mapToPositionDetailDTO(positionService.getPositionDetail(positionId));
        } catch (PositionNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public PositionDetailDTO updatePosition(long positionId, PositionDetailDTO data) {
        try {
            return mapToPositionDetailDTO(positionService.updatePosition(mapToPosition(data)));
        } catch (PositionInvalidException | PositionNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private PositionDTO mapToPositionDTO(Position position) {
        final PositionDTO positionDTO = new PositionDTO();
        positionDTO.setId(position.getId());
        positionDTO.setTitle(position.getTitle());
        positionDTO.setDescription(position.getDescription());

        return positionDTO;
    }

    private static PositionDetailDTO mapToPositionDetailDTO(Position position) {
        final PositionDetailDTO positionDetailDTO = new PositionDetailDTO();
        positionDetailDTO.setId(position.getId());
        positionDetailDTO.setTitle(position.getTitle());
        positionDetailDTO.setDescription(position.getDescription());

        if (position.getRequirements() != null) {
            positionDetailDTO.setRequirements(position.getRequirements().stream()
                .map(PosManagerRestService::mapToRequirementDTO)
                .toList());
        }

        if (position.getConditions() != null) {
            positionDetailDTO.setConditions(position.getConditions().stream()
                .map(PosManagerRestService::mapToConditionDTO)
                .toList());
        }

        return positionDetailDTO;
    }

    private static RequirementDTO mapToRequirementDTO(Requirement requirement) {
        final RequirementDTO requirementDTO = new RequirementDTO();
        requirementDTO.setDescription(requirement.getDescription());

        return requirementDTO;
    }

    private static ConditionDTO mapToConditionDTO(Condition condition) {
        final ConditionDTO conditionDTO = new ConditionDTO();
        conditionDTO.setDescription(condition.getDescription());

        return conditionDTO;
    }

    private static Position mapToPosition(PositionDetailDTO positionDetailDTO) {
        final List<Requirement> requirements = positionDetailDTO.getRequirements().stream()
            .map(PosManagerRestService::mapToRequirement)
            .toList();

        final List<Condition> conditions = positionDetailDTO.getConditions().stream()
            .map(PosManagerRestService::mapToCondition)
            .toList();

        return new Position(
            positionDetailDTO.getId(),
            positionDetailDTO.getTitle(),
            positionDetailDTO.getDescription(),
            requirements,
            conditions);
    }

    private static Requirement mapToRequirement(RequirementDTO requirementDTO) {
        return new Requirement(requirementDTO.getDescription());
    }

    private static Condition mapToCondition(ConditionDTO conditionDTO) {
        return new Condition(conditionDTO.getDescription());
    }
}
