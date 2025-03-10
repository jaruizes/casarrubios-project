package com.jaruiz.casarrubios.recruiters.services.applications.business.ports;

import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.CVNotFoundException;

public interface CVServicePort {
    byte[] getCV(UUID applicationId) throws CVNotFoundException;
}
