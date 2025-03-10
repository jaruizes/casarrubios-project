package com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions;

import lombok.Getter;

@Getter public class TextExtractingException extends Exception {

    public static final String CODE_TEXT_EXTRACT_ERROR = "LLM0001";
    private static final long serialVersionUID = 1L;
    private final String code;

    public TextExtractingException(String message) {
        super(message);
        this.code = CODE_TEXT_EXTRACT_ERROR;
    }

}
