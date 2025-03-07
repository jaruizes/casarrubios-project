package com.jaruiz.casarrubios.recruiters.services.applications.adapters.positions.persistence.postgresql.util;

import java.util.List;

import com.jaruiz.casarrubios.recruiters.services.applications.adapters.positions.persistence.postgresql.entities.BenefitEntity;
import com.jaruiz.casarrubios.recruiters.services.applications.adapters.positions.persistence.postgresql.entities.PositionEntity;
import com.jaruiz.casarrubios.recruiters.services.applications.adapters.positions.persistence.postgresql.entities.RequirementEntity;
import com.jaruiz.casarrubios.recruiters.services.applications.adapters.positions.persistence.postgresql.entities.TaskEntity;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Benefit;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Position;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Requirement;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Task;

public class PositionMapper {
    public static Position positionEntityToPosition(PositionEntity positionEntity) {
        List<Benefit> benefits = positionEntity.getBenefits().stream().map(PositionMapper::conditionsEntityToConditions).toList();
        List<Requirement> requirements = positionEntity.getRequirements().stream().map(PositionMapper::requirementsEntityToRequirements).toList();
        List<Task> tasks = positionEntity.getTasks().stream().map(PositionMapper::taskEntityToTask).toList();

        return new Position(positionEntity.getId(),
            positionEntity.getTitle(),
            positionEntity.getDescription(),
            positionEntity.getTags(),
            positionEntity.getCreatedAt(),
            requirements, benefits,
            tasks);
    }

    private static Requirement requirementsEntityToRequirements(RequirementEntity requirementEntity) {
        return new Requirement(requirementEntity.getKey(),
            requirementEntity.getValue(),
            requirementEntity.getDescription(),
            requirementEntity.getMandatory());
    }

    private static Benefit conditionsEntityToConditions(BenefitEntity benefitEntity) {
        return new Benefit(benefitEntity.getId(),
            benefitEntity.getDescription());
    }

    private static Task taskEntityToTask(TaskEntity taskEntity) {
        return new Task(taskEntity.getId(),
            taskEntity.getDescription());
    }
}
