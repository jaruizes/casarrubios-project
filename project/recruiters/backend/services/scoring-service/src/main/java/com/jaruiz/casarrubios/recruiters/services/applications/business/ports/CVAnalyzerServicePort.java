package com.jaruiz.casarrubios.recruiters.services.applications.business.ports;

import com.jaruiz.casarrubios.recruiters.services.applications.business.model.CVAnalysis;

public interface CVAnalyzerServicePort {
    CVAnalysis analyze(String resumeText);
}
