package com.jaruiz.casarrubios.candidates.services.applications.adapters.persistence.postgresql;

import com.jaruiz.casarrubios.candidates.services.applications.adapters.persistence.postgresql.entities.ApplicationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosgresqlRepository extends CrudRepository<ApplicationEntity, Long> {
}
