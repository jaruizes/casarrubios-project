package com.jaruiz.casarrubios.candidates.services.applications.business.ports;

import com.jaruiz.casarrubios.candidates.services.applications.business.model.Application;

public interface MetadataStoragePort {

    void saveMetada(Application application);
}
