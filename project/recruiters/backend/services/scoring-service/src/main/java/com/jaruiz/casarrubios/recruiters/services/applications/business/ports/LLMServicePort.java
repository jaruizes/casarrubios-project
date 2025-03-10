package com.jaruiz.casarrubios.recruiters.services.applications.business.ports;

import com.jaruiz.casarrubios.recruiters.services.applications.business.model.ResumeAnalysis;

public interface LLMServicePort {
    ResumeAnalysis analyze(String resumeText);
}
