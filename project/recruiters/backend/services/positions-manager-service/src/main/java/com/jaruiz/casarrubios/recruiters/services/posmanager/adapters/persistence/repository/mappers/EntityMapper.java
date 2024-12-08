package com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.entities.ConditionEntity;
import com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.entities.PositionEntity;
import com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.entities.RequirementEntity;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.models.Condition;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.models.Position;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.models.Requirement;

public class EntityMapper {
    public static PositionEntity buildPositionEntity(Position position) {
        final PositionEntity positionEntity = new PositionEntity();
        positionEntity.setId(position.getId());
        positionEntity.setTitle(position.getTitle());
        positionEntity.setDescription(position.getDescription());

        positionEntity.setRequirements(buildRequirementEntities(position.getRequirements(), positionEntity));
        positionEntity.setConditions(buildConditionEntities(position.getConditions(), positionEntity));

        return positionEntity;
    }

    public static Position buildPosition(PositionEntity positionEntity, boolean includeVO) {
        List<Requirement> requirements = new ArrayList<>();
        List<Condition> conditions = new ArrayList<>();

        if (includeVO) {
            requirements = buildRequirements(positionEntity.getRequirements());
            conditions = buildConditions(positionEntity.getConditions());
        }

        return new Position(
            positionEntity.getId(),
            positionEntity.getTitle(),
            positionEntity.getDescription(),
            requirements,
            conditions);
    }

    private static List<RequirementEntity> buildRequirementEntities(List<Requirement> requirements, PositionEntity positionEntity) {
        return requirements.stream()
                           .map(requirement -> buildRequirementEntity(requirement, positionEntity))
                           .collect(Collectors.toList());
    }

    private static List<ConditionEntity> buildConditionEntities(List<Condition> conditions, PositionEntity positionEntity) {
        return conditions.stream()
                         .map(condition -> buildConditionEntity(condition, positionEntity))
                         .collect(Collectors.toList());
    }

    private static RequirementEntity buildRequirementEntity(Requirement requirement, PositionEntity positionEntity) {
        final RequirementEntity requirementEntity = new RequirementEntity();
        requirementEntity.setDescription(requirement.getDescription());
        requirementEntity.setPosition(positionEntity);

        return requirementEntity;
    }

    private static ConditionEntity buildConditionEntity(Condition condition, PositionEntity positionEntity) {
        final ConditionEntity conditionEntity = new ConditionEntity();
        conditionEntity.setDescription(condition.getDescription());
        conditionEntity.setPosition(positionEntity);

        return conditionEntity;
    }

    private static List<Requirement> buildRequirements(List<RequirementEntity> requirementEntities) {
        return requirementEntities.stream()
                                  .map(EntityMapper::buildRequirement)
                                  .collect(Collectors.toList());
    }

    private static List<Condition> buildConditions(List<ConditionEntity> conditionEntities) {
        return conditionEntities.stream()
                                .map(EntityMapper::buildCondition)
                                .collect(Collectors.toList());
    }

    private static Requirement buildRequirement(RequirementEntity requirementEntity) {
        return new Requirement(requirementEntity.getDescription());
    }

    private static Condition buildCondition(ConditionEntity conditionEntity) {
        return new Condition(conditionEntity.getDescription());
    }
}
