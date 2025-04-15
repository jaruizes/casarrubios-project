package com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions;

import java.io.Serial;

import org.jboss.logging.Logger;

public class PositionInvalidException extends RuntimeException {
    private static final Logger logger = Logger.getLogger(PositionInvalidException.class);

    @Serial private static final long serialVersionUID = 1L;
    private final String code;

    public PositionInvalidException() {
        super("Position is invalid");
        this.code = "INVALID_POSITION";

        logger.error("Position is invalid");
    }

    public String getCode() {
        return code;
    }
}
