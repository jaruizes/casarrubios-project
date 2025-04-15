package com.jaruiz.casarrubios.recruiters.services.applications.business.ports;

import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.TextExtractingException;

public interface TextExtractorPort {
    String extractTextFromCV(UUID applicationId, byte[] cv) throws TextExtractingException;
}
