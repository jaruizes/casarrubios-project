package com.jaruiz.casarrubios.recruiters.services.posmanager.business.impl;

import java.util.List;

import com.jaruiz.casarrubios.recruiters.services.posmanager.business.PositionManagerService;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionInvalidException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.Position;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.PositionData;
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

    @Override public Position updatePosition(Position position) throws PositionInvalidException, PositionNotFoundException {
        logger.info("Updating position with Id: " + position.getId());

        checkIfPositionIsValid(position.getData());
        checkIfPositionExists(position.getId());

        this.persistencePort.savePosition(position);

        logger.info("Position with Id: " + position.getId() + " updated");
        return position;
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

    @Override public List<Position> getAllPositions() {
        logger.info("Getting all positions");
        return this.persistencePort.findAllPositions();
    }

    private static void checkIfPositionIsValid(PositionData data) throws PositionInvalidException {
        if (!data.isValid()) {
            logger.error("Position data is not valid");
            throw new PositionInvalidException();
        }
    }

    private void checkIfPositionExists(long id) throws PositionNotFoundException {
        if (this.persistencePort.findPositionById(id) == null) {
            logger.error("Position with Id: " + id + " not found");
            throw new PositionNotFoundException(id);
        }
    }
}
