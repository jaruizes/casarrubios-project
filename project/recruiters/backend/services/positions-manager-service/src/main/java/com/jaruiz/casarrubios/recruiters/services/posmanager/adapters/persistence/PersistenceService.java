package com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence;

import java.util.List;

import com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.PositionsRepository;
import com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.entities.PositionEntity;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.models.Position;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.ports.PersistencePort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import static com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.mappers.EntityMapper.buildPosition;
import static com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.mappers.EntityMapper.buildPositionEntity;

@ApplicationScoped
public class PersistenceService implements PersistencePort {

    private final PositionsRepository positionsRepository;

    public PersistenceService(PositionsRepository positionsRepository) {
        this.positionsRepository = positionsRepository;
    }

    @Override
    @Transactional
    public long savePosition(Position position) {
        if (position.getId() == null) {
            PositionEntity positionEntity = buildPositionEntity(position);
            this.positionsRepository.persist(positionEntity);
            return positionEntity.getId();
        }

        this.positionsRepository.getEntityManager().merge(buildPositionEntity(position));
        return 0;
    }

    @Override
    public Position findPositionById(long id) {
        Position positionFound = null;
        final PositionEntity positionEntity = this.positionsRepository.findById(id);
        if (positionEntity != null) {
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
}
