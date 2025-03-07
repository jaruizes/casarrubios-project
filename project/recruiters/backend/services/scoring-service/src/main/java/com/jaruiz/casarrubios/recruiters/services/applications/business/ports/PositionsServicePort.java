package com.jaruiz.casarrubios.recruiters.services.applications.business.ports;

import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Position;

public interface PositionsServicePort {
    Position getPositionById(long positionId);
}
