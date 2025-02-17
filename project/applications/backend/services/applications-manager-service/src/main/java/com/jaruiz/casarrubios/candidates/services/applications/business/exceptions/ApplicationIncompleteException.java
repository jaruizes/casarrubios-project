package com.jaruiz.casarrubios.candidates.services.applications.business.exceptions;

import java.io.Serial;

public class ApplicationIncompleteException extends Exception {

    @Serial private static final long serialVersionUID = 1L;


    public ApplicationIncompleteException() {
        super("Application is incomplete");
    }
}
