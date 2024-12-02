package com.jaruiz.casarrubios.candidates.services.positions.business.ports;

import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positions.business.model.Position;

public interface PersistencePort {
    Position getPositionById(long positionId);
    List<Position> getAllPositions();
}
