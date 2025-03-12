package com.jaruiz.casarrubios.recruiters.services.applications.business.ports;

import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.applications.business.model.ResumeAnalysis;

public interface ApplicationAnalyzerEventsPublisherPort {
    void sendApplicationAnalysedEvent(UUID applicationId, long positionId, ResumeAnalysis resumeAnalysis);
    void sendToDQL(UUID applicationId, String code, String message);
}
