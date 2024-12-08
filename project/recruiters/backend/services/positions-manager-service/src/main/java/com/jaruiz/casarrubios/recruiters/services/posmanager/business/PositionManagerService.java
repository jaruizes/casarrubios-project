package com.jaruiz.casarrubios.recruiters.services.posmanager.business;

import java.util.List;

import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionInvalidException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.models.Position;

public interface PositionManagerService {

    Position createPosition(Position position) throws PositionInvalidException;
    Position updatePosition(Position position) throws PositionInvalidException, PositionNotFoundException;
    void deletePosition(long id);
    Position getPositionDetail(long positionId) throws PositionNotFoundException;
    List<Position> getAllPositions();
}
