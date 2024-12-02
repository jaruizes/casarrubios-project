package com.jaruiz.casarrubios.candidates.services.applications.business;

import java.util.UUID;

import com.jaruiz.casarrubios.candidates.services.applications.business.exceptions.ApplicationIncompleteException;
import com.jaruiz.casarrubios.candidates.services.applications.business.model.Application;
import com.jaruiz.casarrubios.candidates.services.applications.business.ports.FileStoragePort;
import com.jaruiz.casarrubios.candidates.services.applications.business.ports.MetadataStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ApplicationsService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationsService.class);

    private final FileStoragePort fileStorage;
    private final MetadataStoragePort metadataStorage;

    public ApplicationsService(FileStoragePort fileStorage, MetadataStoragePort metadataStorage) {
        this.fileStorage = fileStorage;
        this.metadataStorage = metadataStorage;
    }


    public UUID uploadCV(Application application) throws ApplicationIncompleteException {
        logger.info("Uploading CV [positionId = {}, name = {}, surname = {}]", application.getPositionId(), application.getName(), application.getSurname());

        if (!application.isComplete()) {
            logger.error("Application is incomplete");
            throw new ApplicationIncompleteException(application.getId());
        }

        final String versionId = this.fileStorage.storeFile(application);
        this.metadataStorage.saveMetada(application);

        logger.info("CV uploaded [positionId = {}, name = {}, surname = {}, versionId = {}]", application.getPositionId(), application.getName(), application.getSurname(), versionId);

        return application.getId();
    }

}
