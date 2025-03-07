package com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions;

import java.util.UUID;

public class CVNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public CVNotFoundException(UUID applicationId) {
        super("Application file with id " + applicationId + " is not saved to file storage");
    }
}
