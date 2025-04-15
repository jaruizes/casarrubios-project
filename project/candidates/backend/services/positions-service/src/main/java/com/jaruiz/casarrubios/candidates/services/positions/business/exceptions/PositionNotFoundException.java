package com.jaruiz.casarrubios.candidates.services.positions.business.exceptions;

import java.io.Serial;

public class PositionNotFoundException extends Exception {

    @Serial private static final long serialVersionUID = 1L;

    public PositionNotFoundException(long positionId) {
        super(String.format("Position with id %d not found", positionId));
    }
}
