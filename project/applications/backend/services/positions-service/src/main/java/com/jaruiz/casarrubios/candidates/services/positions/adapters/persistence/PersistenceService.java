package com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence;

import java.util.ArrayList;
import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.PostgresRepository;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.ConditionEntity;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.PositionEntity;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.RequirementEntity;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Condition;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Position;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.PositionsList;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Requirement;
import com.jaruiz.casarrubios.candidates.services.positions.business.ports.PersistencePort;
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

    private Position positionEntityToPosition(PositionEntity positionEntity) {
        return positionEntityToPosition(positionEntity, true);
    }

    private Position positionEntityToPosition(PositionEntity positionEntity, boolean includeRequirementsAndConditions) {
        List<Condition> conditions = new ArrayList<>();
        List<Requirement> requirements = new ArrayList<>();
        if (includeRequirementsAndConditions) {
            conditions = positionEntity.getConditions().stream().map(this::conditionsEntityToConditions).toList();
            requirements = positionEntity.getRequirements().stream().map(this::requirementsEntityToRequirements).toList();
        }

        return new Position(positionEntity.getId(),
            positionEntity.getTitle(),
            positionEntity.getDescription(),
            requirements,
            conditions);
    }

    private Requirement requirementsEntityToRequirements(RequirementEntity requirementEntity) {
        return new Requirement(requirementEntity.getId(),
            requirementEntity.getDescription());
    }

    private Condition conditionsEntityToConditions(ConditionEntity conditionEntity) {
        return new Condition(conditionEntity.getId(),
            conditionEntity.getDescription());
    }

}
