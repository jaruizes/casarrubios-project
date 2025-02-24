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
        positionEntity.id = position.getId();
        positionEntity.title = position.getTitle();
        positionEntity.description = position.getDescription();
        positionEntity.status = position.getStatus().ordinal();
        positionEntity.createdAt = position.getCreatedAt();
        positionEntity.publishedAt = position.getPublishedAt();

        positionEntity.requirements = buildRequirementEntities(position.getRequirements(), positionEntity);
        positionEntity.benefits = buildConditionEntities(position.getBenefits(), positionEntity);
        positionEntity.tasks = buildTaskEntities(position.getTasks(), positionEntity);

        return positionEntity;
    }

    public static Position buildPosition(PositionEntity positionEntity, boolean includeVO) {
        final PositionData data = new PositionData(positionEntity.title, positionEntity.description);
        if (includeVO) {
            data.addRequirements(buildRequirements(positionEntity.requirements));
            data.addBenefits(buildBenefits(positionEntity.benefits));
            data.addTasks(buildTasks(positionEntity.tasks));
        }

        final Position position = new Position(positionEntity.id, data, positionEntity.createdAt, positionEntity.publishedAt);
        position.setStatus(PositionStatus.values()[positionEntity.status]);

        return position;
    }

    private static List<RequirementEntity> buildRequirementEntities(List<Requirement> requirements, PositionEntity positionEntity) {
        return requirements.stream()
                           .map(requirement -> buildRequirementEntity(requirement, positionEntity))
                           .collect(Collectors.toList());
    }

    private static List<BenefitEntity> buildConditionEntities(List<Benefit> benefits, PositionEntity positionEntity) {
        return benefits.stream()
                       .map(benefit -> buildBenefitEntity(benefit, positionEntity))
                       .collect(Collectors.toList());
    }

    private static List<TaskEntity> buildTaskEntities(List<Task> tasks, PositionEntity positionEntity) {
        return tasks.stream()
                    .map(task -> buildTask(task, positionEntity))
                    .collect(Collectors.toList());
    }

    private static RequirementEntity buildRequirementEntity(Requirement requirement, PositionEntity positionEntity) {
        final RequirementEntity requirementEntity = new RequirementEntity();
        requirementEntity.description = requirement.getDescription();
        requirementEntity.position = positionEntity;
        requirementEntity.key = requirement.getKey();
        requirementEntity.value = requirement.getValue();
        requirementEntity.mandatory = requirement.isMandatory();

        return requirementEntity;
    }

    private static BenefitEntity buildBenefitEntity(Benefit benefit, PositionEntity positionEntity) {
        final BenefitEntity benefitEntity = new BenefitEntity();
        benefitEntity.description = benefit.getDescription();
        benefitEntity.position = positionEntity;

        return benefitEntity;
    }

    private static TaskEntity buildTask(Task task, PositionEntity positionEntity) {
        final TaskEntity taskEntity = new TaskEntity();
        taskEntity.description = task.getDescription();
        taskEntity.position = positionEntity;
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
        return new Requirement(requirementEntity.key, requirementEntity.value, requirementEntity.description, requirementEntity.mandatory);
    }

    private static Benefit buildBenefit(BenefitEntity benefitEntity) {
        return new Benefit(benefitEntity.description);
    }

    private static Task buildTask(TaskEntity taskEntity) {
        return new Task(taskEntity.description);
    }
}
