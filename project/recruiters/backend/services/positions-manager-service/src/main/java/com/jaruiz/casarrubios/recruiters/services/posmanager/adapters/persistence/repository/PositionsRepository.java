package com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository;

import com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.entities.PositionEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PositionsRepository implements PanacheRepository<PositionEntity> {
}
