package com.jaruiz.casarrubios.recruiters.services.applications.business;

import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.AnalysingException;
import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.CVNotFoundException;
import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.TextExtractingException;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Application;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.ResumeAnalysis;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.CVServicePort;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.LLMServicePort;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.TextExtractorPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ApplicationsAnalyzerService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationsAnalyzerService.class);

    private final CVServicePort cvService;
    private final TextExtractorPort textExtractor;
    private final LLMServicePort cvAnalyzerService;

    public ApplicationsAnalyzerService(CVServicePort cvService, TextExtractorPort textExtractor, LLMServicePort cvAnalyzerService) {
        this.cvService = cvService;
        this.textExtractor = textExtractor;
        this.cvAnalyzerService = cvAnalyzerService;
    }

    public ResumeAnalysis analyzeApplication(Application application) throws CVNotFoundException, AnalysingException, TextExtractingException {
        final String cvText = getTextFromCv(application.getId());
        logger.info("Analyzed CV with id {} successfully", application.getId());
        return cvAnalyzerService.analyze(application.getId(), cvText);
    }

    private String getTextFromCv(UUID applicationId) throws CVNotFoundException, TextExtractingException {
        byte[] cv = cvService.getCV(applicationId);
        return textExtractor.extractTextFromCV(applicationId, cv);
    }
}
