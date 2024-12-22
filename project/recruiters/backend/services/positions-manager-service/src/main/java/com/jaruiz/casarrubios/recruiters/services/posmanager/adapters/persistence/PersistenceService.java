package com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence;

import java.util.List;

import com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.PositionsRepository;
import com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.entities.PositionEntity;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.Position;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.ports.PersistencePort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import static com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.mappers.EntityMapper.buildPosition;
import static com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.mappers.EntityMapper.buildPositionEntity;

@ApplicationScoped
public class PersistenceService implements PersistencePort {
    private static final Logger logger = Logger.getLogger(PersistenceService.class);

    private final PositionsRepository positionsRepository;

    public PersistenceService(PositionsRepository positionsRepository) {
        this.positionsRepository = positionsRepository;
    }

    @Override
    @Transactional
    public Position savePosition(Position position) {
        PositionEntity newPositionEntity = buildPositionEntity(position);
        if (position.getId() == null) {
            savePositionEntity(newPositionEntity);
        } else {
            updatePositionEntity(newPositionEntity);
        }

        return buildPosition(newPositionEntity, true);
    }

    @Override
    public Position findPositionById(long id) {
        Position positionFound = null;
        final PositionEntity positionEntity = this.positionsRepository.findById(id);
        if (positionEntity != null) {
            logger.debug("[PERSISTENCE] Position with Id: " + id + " found in database");
            positionFound = buildPosition(positionEntity, true);
        }

        return positionFound;
    }

    @Override
    public List<Position> findAllPositions() {
        return this.positionsRepository.listAll().stream()
                                       .map(positionEntity -> buildPosition(positionEntity, false))
                                       .toList();
    }

    @Override
    public void deletePosition(long id) {
        this.positionsRepository.deleteById(id);
    }

    private PositionEntity savePositionEntity(PositionEntity positionEntity) {
        this.positionsRepository.persist(positionEntity);

        logger.debug("[PERSISTENCE] Position with Id: " + positionEntity.getId() + " saved in database");
        return positionEntity;
    }

    private PositionEntity updatePositionEntity(PositionEntity positionEntity) {
        this.positionsRepository.getEntityManager().merge(positionEntity);

        logger.debug("[PERSISTENCE] Position with Id: " + positionEntity.getId() + " updated in database");
        return positionEntity;
    }
}
