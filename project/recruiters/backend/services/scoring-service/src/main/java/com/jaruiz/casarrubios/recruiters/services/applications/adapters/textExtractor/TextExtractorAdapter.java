package com.jaruiz.casarrubios.recruiters.services.applications.adapters.textExtractor;

import java.io.ByteArrayInputStream;

import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.TextExtractorPort;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

@Service
public class TextExtractorAdapter implements TextExtractorPort {
    private final Tika tika = new Tika();

    @Override
    public String extractTextFromCV(byte[] cv) {
        try {
            return tika.parseToString(new ByteArrayInputStream(cv));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
