package com.jaruiz.casarrubios.candidates.services.positionsservice.business.ports;

import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positionsservice.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.model.Position;

public interface PositionServicePort {

    Position getPositionDetail(long positionId) throws PositionNotFoundException;
    List<Position> getPositions();
}
