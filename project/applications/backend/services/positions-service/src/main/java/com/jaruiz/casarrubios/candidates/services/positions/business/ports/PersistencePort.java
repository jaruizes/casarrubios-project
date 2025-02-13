package com.jaruiz.casarrubios.candidates.services.positions.business.ports;

import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positions.business.model.Position;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.PositionsList;

public interface PersistencePort {
    Position getPositionById(long positionId);
    PositionsList getAllPositions(int page, int size);
}
