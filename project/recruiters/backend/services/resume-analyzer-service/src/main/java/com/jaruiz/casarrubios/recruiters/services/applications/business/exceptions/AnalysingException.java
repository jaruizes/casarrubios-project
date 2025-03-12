package com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions;

import lombok.Getter;

@Getter public class AnalysingException extends Exception {

    public static final String CODE_LLM_RESPONSE_NULL_OR_EMPTY = "LLM0001";
    public static final String CODE_LLM_RESPONSE_PARSING_ERROR = "LLM0002";
    private static final long serialVersionUID = 1L;
    private final String code;

    public AnalysingException(String message, String code) {
        super(message);
        this.code = code;
    }

}
