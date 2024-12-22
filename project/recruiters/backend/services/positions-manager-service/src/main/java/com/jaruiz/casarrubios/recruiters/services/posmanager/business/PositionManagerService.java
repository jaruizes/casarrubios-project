package com.jaruiz.casarrubios.recruiters.services.posmanager.business;

import java.util.List;

import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionInvalidException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.Position;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.PositionData;

public interface PositionManagerService {

    Position createPosition(PositionData data) throws PositionInvalidException;
    Position updatePosition(Position position) throws PositionInvalidException, PositionNotFoundException;
    void deletePosition(long id) throws PositionNotFoundException;
    Position getPositionDetail(long positionId) throws PositionNotFoundException;
    List<Position> getAllPositions();
}
