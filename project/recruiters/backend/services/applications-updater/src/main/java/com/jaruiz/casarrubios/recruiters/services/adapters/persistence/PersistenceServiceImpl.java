package com.jaruiz.casarrubios.recruiters.services.adapters.persistence;

import java.sql.Timestamp;

import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.ApplicationsRepository;
import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.entities.ApplicationEntity;
import com.jaruiz.casarrubios.recruiters.services.business.model.Application;
import com.jaruiz.casarrubios.recruiters.services.business.ports.PersistenceService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PersistenceServiceImpl implements PersistenceService {

    private static final Logger logger = Logger.getLogger(PersistenceServiceImpl.class);
    private final ApplicationsRepository applicationsRepository;

    public PersistenceServiceImpl(ApplicationsRepository applicationsRepository) {
        this.applicationsRepository = applicationsRepository;
    }

    @Transactional
    @Override
    public void saveApplication(Application application) {
        logger.info("Saving application into database with Id: " + application.getId());
        this.applicationsRepository.persist(mapToEntity(application));

        logger.info("Application with Id: " + application.getId() + " saved into database");
    }

    private ApplicationEntity mapToEntity(Application application) {
        ApplicationEntity applicationEntity = new ApplicationEntity();
        applicationEntity.setId(application.getId());
        applicationEntity.setName(application.getName());
        applicationEntity.setEmail(application.getEmail());
        applicationEntity.setPhone(application.getPhone());
        applicationEntity.setCv(application.getCv());
        applicationEntity.setPositionId(application.getPositionId());
        applicationEntity.setCreatedAt(new Timestamp(application.getCreatedAt()));
        return applicationEntity;
    }
}
