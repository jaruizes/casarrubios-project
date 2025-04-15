package com.jaruiz.casarrubios.candidates.services.applications.business.ports;

import com.jaruiz.casarrubios.candidates.services.applications.business.exceptions.ApplicationFileNotStoredException;
import com.jaruiz.casarrubios.candidates.services.applications.business.model.Application;

public interface FileStoragePort {
    String storeFile(Application application) throws ApplicationFileNotStoredException;
    void deleteFile(String path);
}
