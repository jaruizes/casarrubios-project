package com.jaruiz.casarrubios.candidates.services.positionsservice.business.ports;

import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positionsservice.business.model.Position;

public interface PersistencePort {
    Position getPositionById(long positionId);
    List<Position> getAllPositions();
}
