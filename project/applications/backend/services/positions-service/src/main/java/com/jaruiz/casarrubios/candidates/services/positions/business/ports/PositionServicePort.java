package com.jaruiz.casarrubios.candidates.services.positions.business.ports;

import com.jaruiz.casarrubios.candidates.services.positions.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Position;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.PositionsList;

public interface PositionServicePort {

    Position getPositionDetail(long positionId) throws PositionNotFoundException;
    PositionsList getPositions(int page, int pageSize);
}
