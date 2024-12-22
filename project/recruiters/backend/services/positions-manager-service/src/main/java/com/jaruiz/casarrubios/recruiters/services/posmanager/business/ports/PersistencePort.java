package com.jaruiz.casarrubios.recruiters.services.posmanager.business.ports;

import java.util.List;

import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.Position;

public interface PersistencePort {
    Position savePosition(Position position);
    Position findPositionById(long id);
    List<Position> findAllPositions();
    void deletePosition(long id);
}
