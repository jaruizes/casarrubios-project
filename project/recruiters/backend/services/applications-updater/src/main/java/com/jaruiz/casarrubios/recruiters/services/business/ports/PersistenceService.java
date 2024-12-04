package com.jaruiz.casarrubios.recruiters.services.business.ports;

import com.jaruiz.casarrubios.recruiters.services.business.model.Application;

public interface PersistenceService {

    void saveApplication(Application application);
}
