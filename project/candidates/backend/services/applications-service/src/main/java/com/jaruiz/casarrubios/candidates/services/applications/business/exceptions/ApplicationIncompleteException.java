package com.jaruiz.casarrubios.candidates.services.applications.business.exceptions;

import java.util.UUID;

import com.jaruiz.casarrubios.candidates.services.applications.business.model.Application;

public class ApplicationIncompleteException extends Exception {

    private static final long serialVersionUID = 1L;

    public ApplicationIncompleteException(UUID applicationId) {
        super("Application with id " + applicationId + " is incomplete");
    }
}
