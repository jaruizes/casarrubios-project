package com.jaruiz.casarrubios.recruiters.services.business.impl;

import com.jaruiz.casarrubios.recruiters.services.business.ApplicationsUpdaterService;
import com.jaruiz.casarrubios.recruiters.services.business.model.Application;
import com.jaruiz.casarrubios.recruiters.services.business.ports.PersistenceService;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ApplicationsUpdaterServiceImpl implements ApplicationsUpdaterService {

    private static final Logger logger = Logger.getLogger(ApplicationsUpdaterServiceImpl.class);

    private final PersistenceService persistenceService;

    public ApplicationsUpdaterServiceImpl(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @Override
    public void processApplication(Application application) {
        logger.info("Processing application with Id: " + application.getId());

        this.persistenceService.saveApplication(application);

        logger.info("Application with Id: " + application.getId() + " processed");
    }
}
