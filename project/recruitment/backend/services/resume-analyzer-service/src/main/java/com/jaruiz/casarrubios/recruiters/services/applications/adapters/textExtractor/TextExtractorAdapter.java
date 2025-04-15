package com.jaruiz.casarrubios.recruiters.services.applications.adapters.textExtractor;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.TextExtractingException;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.TextExtractorPort;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TextExtractorAdapter implements TextExtractorPort {
    private static final Logger logger = LoggerFactory.getLogger(TextExtractorAdapter.class);
    private final Tika tika = new Tika();

    @Override
    public String extractTextFromCV(UUID applicationId, byte[] cv) throws TextExtractingException {
        try {
            return tika.parseToString(new ByteArrayInputStream(cv));
        } catch (Exception e) {
            logger.error("Error extracting text from CV with id {}", applicationId);
            logger.error(e.getMessage());
            throw new TextExtractingException(applicationId.toString());
        }
    }
}
