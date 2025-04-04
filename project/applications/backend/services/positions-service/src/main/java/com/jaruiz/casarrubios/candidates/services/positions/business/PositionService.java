package com.jaruiz.casarrubios.candidates.services.positions.business;

import com.jaruiz.casarrubios.candidates.services.positions.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Position;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.PositionsList;
import com.jaruiz.casarrubios.candidates.services.positions.business.ports.PersistencePort;
import com.jaruiz.casarrubios.candidates.services.positions.business.ports.PositionServicePort;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PositionService implements PositionServicePort {

    private static final Logger logger = LoggerFactory.getLogger(PositionService.class);

    private final PersistencePort persistencePort;

    public PositionService(PersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public Position getPositionDetail(long positionId) throws PositionNotFoundException {
        logger.info("Getting position detail for position with id {}", positionId);

        final Position position = this.persistencePort.getPositionById(positionId);
        if (position == null) {
            logger.error("Position with id {} not found", positionId);
            throw new PositionNotFoundException(positionId);
        }

        logger.info("Position with id {} found", positionId);
        return position;
    }

    @Override
    public PositionsList getPositions(int page, int pageSize) {
        logger.info("Getting all positions [page: {}, pageSize: {}]", page, pageSize);

        return this.persistencePort.getAllPositions(page, pageSize);
    }

    @Override
    @Transactional
    public void savePosition(Position position) {
        logger.info("Saving position with id {}", position.getId());

        this.persistencePort.savePosition(position);

        logger.info("Position with id {} saved", position.getId());
    }
}
