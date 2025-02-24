package com.jaruiz.casarrubios.recruiters.services.posmanager.business.ports;

import java.util.List;

import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.Position;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.PositionsList;

public interface PersistencePort {
    Position savePosition(Position position);
    Position findPositionById(long id);
    PositionsList findAllPositions(int page, int pageSize);
    void deletePosition(long id);
}
