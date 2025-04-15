package com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions;

import java.io.Serial;

import org.jboss.logging.Logger;

public class PositionNotFoundException extends RuntimeException {
    private static final Logger logger = Logger.getLogger(PositionNotFoundException.class);

    @Serial private static final long serialVersionUID = 1L;
    private final String code;

    public PositionNotFoundException(long positionId) {
        super("Position not found. ID: " + positionId);
        logger.error("Position not found. ID: " + positionId);

        this.code = "POSITION_NOT_FOUND";
    }

    public String getCode() {
        return code;
    }

}
