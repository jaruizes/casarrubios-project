package com.jaruiz.casarrubios.candidates.services.applications.adapters.persistence;

import com.jaruiz.casarrubios.candidates.services.applications.adapters.persistence.postgresql.PosgresqlRepository;
import com.jaruiz.casarrubios.candidates.services.applications.adapters.persistence.postgresql.entities.ApplicationEntity;
import com.jaruiz.casarrubios.candidates.services.applications.business.model.Application;
import com.jaruiz.casarrubios.candidates.services.applications.business.ports.MetadataStoragePort;
import org.springframework.stereotype.Service;

@Service
public class PersistenceService implements MetadataStoragePort {

    private final PosgresqlRepository posgresqlRepository;

    public PersistenceService(PosgresqlRepository posgresqlRepository) {
        this.posgresqlRepository = posgresqlRepository;
    }

    @Override
    public void saveMetada(Application application) {
        this.posgresqlRepository.save(toEntity(application));
    }

    private ApplicationEntity toEntity(Application application) {
        final ApplicationEntity applicationEntity = new ApplicationEntity();
        applicationEntity.setId(application.getId().toString());
        applicationEntity.setName(application.getName());
        applicationEntity.setEmail(application.getEmail());
        applicationEntity.setPhone(application.getPhone());
        applicationEntity.setCv(application.getFilePath());

        return applicationEntity;
    }
}
