package com.jaruiz.casarrubios.recruiters.services.applications.business.ports;

import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.applications.business.model.ResumeAnalysis;

public interface ApplicationAnalysedProducerPort {
    void sendApplicationAnalysedEvent(UUID applicationId, ResumeAnalysis resumeAnalysis);
}
