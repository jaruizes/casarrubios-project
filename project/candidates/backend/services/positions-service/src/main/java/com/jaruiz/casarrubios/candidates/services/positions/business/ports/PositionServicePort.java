package com.jaruiz.casarrubios.candidates.services.positions.business.ports;

import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positions.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Position;

public interface PositionServicePort {

    Position getPositionDetail(long positionId) throws PositionNotFoundException;
    List<Position> getPositions();
}
