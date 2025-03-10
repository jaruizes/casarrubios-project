package com.jaruiz.casarrubios.recruiters.services.applications.business.ports;

import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.AnalysingException;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.ResumeAnalysis;

public interface LLMServicePort {
    ResumeAnalysis analyze(UUID applicationId, String resumeText) throws AnalysingException;
}
