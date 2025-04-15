package com.jaruiz.casarrubios.recruiters.services.posmanager.business.impl;

import com.jaruiz.casarrubios.recruiters.services.posmanager.business.PositionManagerService;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionInvalidException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.Position;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.PositionData;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.PositionsList;
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

    @Override public Position createPosition(PositionData data) throws PositionInvalidException {
        checkIfPositionIsValid(data);
        logger.info("Creating new position [title: " + data.getTitle() + "]");

        final Position positionToSave = new Position(data);
        final Position positionSaved = this.persistencePort.savePosition(positionToSave);
        logger.info("Position with Id: " + positionSaved.getId() + " created");

        return positionSaved;
    }

    @Override public Position updatePosition(Position positionToUpdate) throws PositionInvalidException, PositionNotFoundException {
        final var id = positionToUpdate.getId();
        logger.info("Updating positionToUpdate with Id: " + id);

        checkIfPositionIsValid(positionToUpdate.getData());
        Position currentPosition = getPositionDetail(id);

        positionToUpdate.setStatus(currentPosition.getStatus());
        positionToUpdate.setPublishedAt(currentPosition.getPublishedAt());
        positionToUpdate.setCreatedAt(currentPosition.getCreatedAt());

        this.persistencePort.savePosition(positionToUpdate);

        logger.info("Position with Id: " + id + " updated");

        return getPositionDetail(id);
    }

    @Override public void deletePosition(long id) throws PositionNotFoundException {
        logger.info("Deleting position with Id: " + id);

        checkIfPositionExists(id);
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

    @Override public PositionsList getAllPositions(int page, int pageSize) {
        logger.info("Getting all positions");
        return this.persistencePort.findAllPositions(page, pageSize);
    }

    private static void checkIfPositionIsValid(PositionData data) throws PositionInvalidException {
        if (!data.isValid()) {
            logger.error("Position data is not valid");
            throw new PositionInvalidException();
        }
    }

    private Position checkIfPositionExists(long id) throws PositionNotFoundException {
        Position position = this.persistencePort.findPositionById(id);
        if (position != null) {
            return position;
        }

        logger.error("Position with Id: " + id + " not found");
        throw new PositionNotFoundException(id);
    }
}
