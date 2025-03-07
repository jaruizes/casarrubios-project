package com.jaruiz.casarrubios.recruiters.services.applications.business.ports;

import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.CVNotFoundException;

public interface CVServicePort {
    byte[] getCV(String applicationId) throws CVNotFoundException;
}
