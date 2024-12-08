package com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions;

import java.io.Serial;

public class PositionInvalidException extends Exception {

    @Serial private static final long serialVersionUID = 1L;
    private final String code;

    public PositionInvalidException() {
        super("Position is invalid");
        this.code = "INVALID_POSITION";
    }

    public String getCode() {
        return code;
    }
}
