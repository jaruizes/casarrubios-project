package com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository;

import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.entities.ApplicationEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationsRepository implements PanacheRepositoryBase<ApplicationEntity, UUID> {

}
