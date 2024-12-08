package com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions;

import java.io.Serial;

public class PositionNotFoundException extends Exception {

    @Serial private static final long serialVersionUID = 1L;
    private final long positionId;

    public PositionNotFoundException(long positionId) {
        super("Position not found. ID: " + positionId);
        this.positionId = positionId;
    }

    public long getPositionId() {
        return positionId;
    }
}
