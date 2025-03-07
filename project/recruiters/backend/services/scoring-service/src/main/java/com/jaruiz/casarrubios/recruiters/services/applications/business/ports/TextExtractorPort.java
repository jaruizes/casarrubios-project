package com.jaruiz.casarrubios.recruiters.services.applications.business.ports;

public interface TextExtractorPort {
    String extractTextFromCV(byte[] cv);
}
