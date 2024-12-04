package com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository;

import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.entities.ApplicationEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationsRepository implements PanacheRepository<ApplicationEntity> {

}
