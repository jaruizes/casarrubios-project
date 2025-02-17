package com.jaruiz.casarrubios.candidates.services.applications.business.exceptions;

import java.util.UUID;

public class ApplicationFileNotStoredException extends Exception {

    private static final long serialVersionUID = 1L;

    public ApplicationFileNotStoredException(UUID applicationId) {
        super("Application file with id " + applicationId + " is not saved to file storage");
    }
}
