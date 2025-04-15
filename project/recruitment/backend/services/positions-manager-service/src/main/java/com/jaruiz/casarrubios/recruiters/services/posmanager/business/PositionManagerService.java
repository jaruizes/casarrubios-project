package com.jaruiz.casarrubios.recruiters.services.posmanager.business;

import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionInvalidException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.Position;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.PositionData;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.PositionsList;

public interface PositionManagerService {

    Position createPosition(PositionData data) throws PositionInvalidException;
    Position updatePosition(Position position) throws PositionInvalidException, PositionNotFoundException;
    void deletePosition(long id) throws PositionNotFoundException;
    Position getPositionDetail(long positionId) throws PositionNotFoundException;
    PositionsList getAllPositions(int page, int pageSize);
}
