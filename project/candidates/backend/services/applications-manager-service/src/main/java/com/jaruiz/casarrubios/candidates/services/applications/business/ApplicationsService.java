package com.jaruiz.casarrubios.candidates.services.applications.business;

import java.util.UUID;

import com.jaruiz.casarrubios.candidates.services.applications.business.exceptions.ApplicationFileNotStoredException;
import com.jaruiz.casarrubios.candidates.services.applications.business.exceptions.ApplicationIncompleteException;
import com.jaruiz.casarrubios.candidates.services.applications.business.exceptions.ApplicationsGeneralException;
import com.jaruiz.casarrubios.candidates.services.applications.business.model.Application;
import com.jaruiz.casarrubios.candidates.services.applications.business.ports.FileStoragePort;
import com.jaruiz.casarrubios.candidates.services.applications.business.ports.MetadataStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service public class ApplicationsService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationsService.class);
    public static final String FILE_NOT_STORED_ERROR = "0001";
    public static final String METADATA_NOT_STORED_ERROR = "0002";

    private final FileStoragePort fileStorage;
    private final MetadataStoragePort metadataStorage;

    public ApplicationsService(FileStoragePort fileStorage, MetadataStoragePort metadataStorage) {
        this.fileStorage = fileStorage;
        this.metadataStorage = metadataStorage;
    }

    public UUID uploadCV(Application application) throws ApplicationsGeneralException, ApplicationIncompleteException {
        logger.info("Uploading CV [positionId = {}, name = {}]", application.getPositionId(), application.getName());

        if (!application.isComplete()) {
            logger.error("Application is incomplete");
            throw new ApplicationIncompleteException();
        }

        try {
            this.fileStorage.storeFile(application);
            logger.info("CV stored [positionId = {}, name = {}, path = {}]", application.getPositionId(), application.getName(),
                application.getFilePath());

            this.metadataStorage.saveMetada(application);
            logger.info("CV metadata saved to database [positionId = {}, name = {}, path = {}]", application.getPositionId(), application.getName(),
                application.getFilePath());

            return application.getId();

        } catch (ApplicationFileNotStoredException e) {
            logger.error("Error storing CV [positionId = {}, name = {}]", application.getPositionId(), application.getName());
            logger.error(e.getMessage());

            throw new ApplicationsGeneralException(application.getId(), FILE_NOT_STORED_ERROR);
        } catch (Exception e) {
            logger.error("Error saving CV metadata to database [positionId = {}, name = {}, path = {}]", application.getPositionId(), application.getName(),
               application.getFilePath());
            logger.error(e.getMessage());

            this.fileStorage.deleteFile(application.getFilePath());
            throw new ApplicationsGeneralException(application.getId(), METADATA_NOT_STORED_ERROR);
        }
    }

}
