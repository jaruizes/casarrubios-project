package com.jaruiz.casarrubios.recruiters.services.posmanager.business.impl;

import java.util.List;

import com.jaruiz.casarrubios.recruiters.services.posmanager.business.PositionManagerService;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionInvalidException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.models.Position;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.ports.PersistencePort;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PositionManagerServiceImpl implements PositionManagerService {

    private static final Logger logger = Logger.getLogger(PositionManagerServiceImpl.class);
    private final PersistencePort persistencePort;

    public PositionManagerServiceImpl(PersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override public Position createPosition(Position position) throws PositionInvalidException {
        logger.info("Creating position with Id: " + position.getId());
        validatePosition(position);

        long positionSavedId = this.persistencePort.savePosition(position);
        logger.info("Position with Id: " + positionSavedId + " created");

        return buildNewPositionObject(position, positionSavedId);
    }

    @Override public Position updatePosition(Position position) throws PositionInvalidException, PositionNotFoundException {
        logger.info("Updating position with Id: " + position.getId());
        validatePosition(position);

        Position positionFound = this.persistencePort.findPositionById(position.getId());
        if (positionFound == null) {
            logger.error("Position with Id: " + position.getId() + " not found");
            throw new PositionNotFoundException(position.getId());
        }

        this.persistencePort.savePosition(position);

        logger.info("Position with Id: " + position.getId() + " updated");
        return position;
    }

    @Override public void deletePosition(long id) {
        logger.info("Deleting position with Id: " + id);
        this.persistencePort.deletePosition(id);
        logger.info("Position with Id: " + id + " deleted");
    }

    @Override public Position getPositionDetail(long positionId) throws PositionNotFoundException {
        logger.info("Getting position detail with Id: " + positionId);

        final Position position = this.persistencePort.findPositionById(positionId);
        if (position == null) {
            throw new PositionNotFoundException(positionId);
        }

        logger.info("Position detail with Id: " + positionId + " retrieved");
        return position;
    }

    @Override public List<Position> getAllPositions() {
        logger.info("Getting all positions");
        return this.persistencePort.findAllPositions();
    }

    private static void validatePosition(Position position) throws PositionInvalidException {
        if (!position.isValid()) {
            logger.error("Position is not valid");
            throw new PositionInvalidException();
        }
    }

    private Position buildNewPositionObject(Position position, long id) {
        return new Position(
            id,
            position.getTitle(),
            position.getDescription(),
            position.getRequirements(),
            position.getConditions()
        );
    }
}
