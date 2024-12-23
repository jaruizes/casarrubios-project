package com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.mappers;

import java.util.List;
import java.util.stream.Collectors;

import com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.entities.BenefitEntity;
import com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.entities.PositionEntity;
import com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.entities.RequirementEntity;
import com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.entities.TaskEntity;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.*;

public class EntityMapper {
    public static PositionEntity buildPositionEntity(Position position) {
        final PositionEntity positionEntity = new PositionEntity();
        positionEntity.setId(position.getId());
        positionEntity.setTitle(position.getTitle());
        positionEntity.setDescription(position.getDescription());
        positionEntity.setStatus(position.getStatus().ordinal());
        positionEntity.setCreatedAt(position.getCreatedAt());
        positionEntity.setPublishedAt(position.getPublishedAt());

        positionEntity.setRequirements(buildRequirementEntities(position.getRequirements(), positionEntity));
        positionEntity.setBenefits(buildConditionEntities(position.getBenefits(), positionEntity));
        positionEntity.setTasks(buildTaskEntities(position.getTasks()));

        return positionEntity;
    }

    public static Position buildPosition(PositionEntity positionEntity, boolean includeVO) {
        final PositionData data = new PositionData(positionEntity.getTitle(), positionEntity.getDescription());
        if (includeVO) {
            data.addRequirements(buildRequirements(positionEntity.getRequirements()));
            data.addBenefits(buildBenefits(positionEntity.getBenefits()));
            data.addTasks(buildTasks(positionEntity.getTasks()));
        }

        final Position position = new Position(positionEntity.getId(), data, positionEntity.getCreatedAt(), positionEntity.getPublishedAt());
        position.setStatus(PositionStatus.values()[positionEntity.getStatus()]);

        return position;
    }

    private static List<RequirementEntity> buildRequirementEntities(List<Requirement> requirements, PositionEntity positionEntity) {
        return requirements.stream()
                           .map(requirement -> buildRequirementEntity(requirement, positionEntity))
                           .collect(Collectors.toList());
    }

    private static List<BenefitEntity> buildConditionEntities(List<Benefit> benefits, PositionEntity positionEntity) {
        return benefits.stream()
                       .map(benefit -> buildConditionEntity(benefit, positionEntity))
                       .collect(Collectors.toList());
    }

    private static List<TaskEntity> buildTaskEntities(List<Task> tasks) {
        return tasks.stream()
                    .map(EntityMapper::buildTask)
                    .collect(Collectors.toList());
    }

    private static RequirementEntity buildRequirementEntity(Requirement requirement, PositionEntity positionEntity) {
        final RequirementEntity requirementEntity = new RequirementEntity();
        requirementEntity.setDescription(requirement.getDescription());
        requirementEntity.setPosition(positionEntity);
        requirementEntity.setKey(requirement.getKey());

        return requirementEntity;
    }

    private static BenefitEntity buildConditionEntity(Benefit benefit, PositionEntity positionEntity) {
        final BenefitEntity benefitEntity = new BenefitEntity();
        benefitEntity.setDescription(benefit.getDescription());
        benefitEntity.setPosition(positionEntity);

        return benefitEntity;
    }

    private static TaskEntity buildTask(Task task) {
        final TaskEntity taskEntity = new TaskEntity();
        taskEntity.setDescription(task.getDescription());
        return taskEntity;
    }

    private static List<Requirement> buildRequirements(List<RequirementEntity> requirementEntities) {
        return requirementEntities.stream()
                                  .map(EntityMapper::buildRequirement)
                                  .collect(Collectors.toList());
    }

    private static List<Benefit> buildBenefits(List<BenefitEntity> conditionEntities) {
        return conditionEntities.stream()
                                .map(EntityMapper::buildBenefit)
                                .collect(Collectors.toList());
    }

    private static List<Task> buildTasks(List<TaskEntity> taskEntities) {
        return taskEntities.stream()
                           .map(EntityMapper::buildTask)
                           .collect(Collectors.toList());
    }

    private static Requirement buildRequirement(RequirementEntity requirementEntity) {
        return new Requirement(requirementEntity.getKey(), requirementEntity.getValue(), requirementEntity.getDescription(), requirementEntity.getMandatory());
    }

    private static Benefit buildBenefit(BenefitEntity benefitEntity) {
        return new Benefit(benefitEntity.getDescription());
    }

    private static Task buildTask(TaskEntity taskEntity) {
        return new Task(taskEntity.getDescription());
    }
}
