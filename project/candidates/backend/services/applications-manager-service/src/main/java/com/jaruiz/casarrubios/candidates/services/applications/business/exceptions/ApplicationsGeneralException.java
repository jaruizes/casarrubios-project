package com.jaruiz.casarrubios.candidates.services.applications.business.exceptions;

import java.io.Serial;
import java.util.UUID;

public class ApplicationsGeneralException extends Exception {

    @Serial private static final long serialVersionUID = 1L;
    private final UUID applicationId;
    private final String code;

    public ApplicationsGeneralException(UUID applicationId, String code) {
        super("Application file with id " + applicationId + " is not processed");
        this.applicationId = applicationId;
        this.code = code;
    }

    public UUID getApplicationId() {
        return applicationId;
    }

    public String getCode() {
        return code;
    }
}
