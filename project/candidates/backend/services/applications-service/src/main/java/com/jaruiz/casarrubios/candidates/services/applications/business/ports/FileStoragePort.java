package com.jaruiz.casarrubios.candidates.services.applications.business.ports;

import com.jaruiz.casarrubios.candidates.services.applications.business.model.Application;

public interface FileStoragePort {
    String storeFile(Application application);
    void deleteFile(String path);
}
