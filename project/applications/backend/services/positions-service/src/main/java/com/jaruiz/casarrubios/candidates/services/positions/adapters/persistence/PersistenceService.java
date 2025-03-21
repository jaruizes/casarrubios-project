package com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence;

import java.util.ArrayList;
import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.PostgresRepository;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.BenefitEntity;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.PositionEntity;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.RequirementEntity;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.TaskEntity;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.*;
import com.jaruiz.casarrubios.candidates.services.positions.business.ports.PersistencePort;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class PersistenceService implements PersistencePort {
    private static final Logger logger = LoggerFactory.getLogger(PersistenceService.class);
    private final PostgresRepository postgresRepository;

    public PersistenceService(PostgresRepository postgresRepository) {
        this.postgresRepository = postgresRepository;
    }

    @Override
    public Position getPositionById(long positionId) {
        logger.debug("Getting position detail for position with id {}", positionId);
        final Position position = this.postgresRepository.findById(positionId)
                                      .map(this::positionEntityToPosition)
                                      .orElseGet(() -> {
                                          logger.error("Position with id {} not found in the database", positionId);
                                          return null;
                                      });

        logger.debug("Position with id {} found in the database", positionId);
        return position;
    }

    @Override
    public PositionsList getAllPositions(int page, int size) {
        logger.debug("Getting all positions [page: {}, size: {}}]", page, size);
        final Page<PositionEntity> positionEntities = this.postgresRepository.findAll(PageRequest.of(page, size));

        var total = positionEntities.getTotalElements();
        logger.debug("Found {} positions in the database", positionEntities.getTotalElements());

        final List<Position> positions = positionEntities.stream().map(this::positionEntityToPosition).toList();
        return new PositionsList(total, page, size, positions);
    }

    @Override
    @Transactional
    public void savePosition(Position position) {
        logger.debug("Saving position with id {}", position.getId());
        this.postgresRepository.save(positionToPositionEntity(position));
    }

    private Position positionEntityToPosition(PositionEntity positionEntity) {
        return positionEntityToPosition(positionEntity, true);
    }

    private Position positionEntityToPosition(PositionEntity positionEntity, boolean includeAllElements) {
        List<Benefits> benefits = new ArrayList<>();
        List<Requirement> requirements = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        if (includeAllElements) {
            benefits = positionEntity.getConditions().stream().map(this::benefitsEntityToConditions).toList();
            requirements = positionEntity.getRequirements().stream().map(this::requirementsEntityToRequirements).toList();
            tasks = positionEntity.getTasks().stream().map(this::taskEntityToTask).toList();
        }

        return new Position(positionEntity.getId(),
            positionEntity.getTitle(),
            positionEntity.getDescription(),
            positionEntity.getTags(),
            positionEntity.getCreatedAt(),
            requirements, benefits,
            tasks);
    }

    private PositionEntity positionToPositionEntity(Position position) {
        PositionEntity positionEntity = new PositionEntity();
        positionEntity.setId(position.getId());
        positionEntity.setTitle(position.getTitle());
        positionEntity.setDescription(position.getDescription());
        positionEntity.setTags(position.getTags());
        positionEntity.setCreatedAt(position.getCreatedAt());
        positionEntity.setRequirements(position.getRequirements().stream().map((req) -> requirementsEntityToRequirementsEntity(req, positionEntity)).toList());
        positionEntity.setConditions(position.getConditions().stream().map((ben) -> conditionsEntityToConditionsEntity(ben, positionEntity)).toList());
        positionEntity.setTasks(position.getTasks().stream().map((task) -> taskEntityToTaskEntity(task, positionEntity)).toList());
        return positionEntity;
    }

    private RequirementEntity requirementsEntityToRequirementsEntity(Requirement requirement, PositionEntity positionEntity) {
        RequirementEntity requirementEntity = new RequirementEntity();
        requirementEntity.setKey(requirement.getKey());
        requirementEntity.setValue(requirement.getValue());
        requirementEntity.setDescription(requirement.getDescription());
        requirementEntity.setMandatory(requirement.getMandatory());
        requirementEntity.setPosition(positionEntity);
        return requirementEntity;
    }

    private BenefitEntity conditionsEntityToConditionsEntity(Benefits benefits, PositionEntity positionEntity) {
        BenefitEntity benefitEntity = new BenefitEntity();
        benefitEntity.setId(benefits.getId());
        benefitEntity.setDescription(benefits.getDescription());
        benefitEntity.setPosition(positionEntity);
        return benefitEntity;
    }

    private TaskEntity taskEntityToTaskEntity(Task task, PositionEntity positionEntity) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(task.getId());
        taskEntity.setDescription(task.getDescription());
        taskEntity.setPosition(positionEntity);
        return taskEntity;
    }

    private Requirement requirementsEntityToRequirements(RequirementEntity requirementEntity) {
        return new Requirement(requirementEntity.getId(),
            requirementEntity.getKey(),
            requirementEntity.getValue(),
            requirementEntity.getDescription(),
            requirementEntity.getMandatory());
    }

    private Benefits benefitsEntityToConditions(BenefitEntity benefitEntity) {
        return new Benefits(benefitEntity.getId(),
            benefitEntity.getDescription());
    }

    private Task taskEntityToTask(TaskEntity taskEntity) {
        return new Task(taskEntity.getId(),
            taskEntity.getDescription());
    }

}
