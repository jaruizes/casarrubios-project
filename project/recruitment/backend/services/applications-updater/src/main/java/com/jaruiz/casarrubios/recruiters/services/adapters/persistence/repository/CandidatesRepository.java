package com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.entities.ApplicationEntity;
import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.entities.CandidateEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CandidatesRepository implements PanacheRepositoryBase<CandidateEntity, UUID> {
    public Optional<CandidateEntity> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
}
