package com.jaruiz.casarrubios.recruiters.services.applications.business;

import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.AnalysingException;
import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.CVNotFoundException;
import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.TextExtractingException;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Application;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.ResumeAnalysis;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.ApplicationAnalyzerEventsPublisherPort;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.CVServicePort;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.LLMServicePort;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.TextExtractorPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ApplicationsAnalyzerService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationsAnalyzerService.class);
    public static final String ERROR_RESUME_NOT_FOUND = "AA0002";


    private final CVServicePort cvService;
    private final TextExtractorPort textExtractor;
    private final LLMServicePort cvAnalyzerService;
    private final ApplicationAnalyzerEventsPublisherPort eventPublisher;

    public ApplicationsAnalyzerService(CVServicePort cvService, TextExtractorPort textExtractor, LLMServicePort cvAnalyzerService, ApplicationAnalyzerEventsPublisherPort eventPublisher) {
        this.cvService = cvService;
        this.textExtractor = textExtractor;
        this.cvAnalyzerService = cvAnalyzerService;
        this.eventPublisher = eventPublisher;
    }

    public void analyzeApplication(Application application) {
        final UUID applicationId = application.getId();
        try {
            final String cvText = getTextFromCv(application.getId());
            final ResumeAnalysis analysis = cvAnalyzerService.analyze(applicationId, cvText);
            logger.info("Analyzed CV with id {} successfully", applicationId);

            this.eventPublisher.sendApplicationAnalysedEvent(applicationId, application.getPositionId(), analysis);
            logger.info("Sent application analyzed event for application with id {}", applicationId);
        } catch (AnalysingException e) {
            logger.error("Error analysing application from LLM[key = {}]", applicationId);
            this.eventPublisher.sendToDQL(applicationId, e.getCode(), e.getMessage());
        } catch (CVNotFoundException e) {
            final String message = "Error processing application [key = " + applicationId + "]. Resume not found in storage";
            logger.error(message);
            this.eventPublisher.sendToDQL(applicationId, ERROR_RESUME_NOT_FOUND, message);
        } catch (TextExtractingException e) {
            logger.error("Error extracting text from application [key = {}]", applicationId);
            this.eventPublisher.sendToDQL(applicationId, e.getCode(), e.getMessage());
        }
    }

    private String getTextFromCv(UUID applicationId) throws CVNotFoundException, TextExtractingException {
        byte[] cv = cvService.getCV(applicationId);
        return textExtractor.extractTextFromCV(applicationId, cv);
    }
}
