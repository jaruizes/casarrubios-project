package com.jaruiz.casarrubios.candidates.services.positionsservice.adapters.persistence.postgresql;

import com.jaruiz.casarrubios.candidates.services.positionsservice.adapters.persistence.postgresql.entities.PositionEntity;
import org.springframework.data.repository.CrudRepository;

public interface PostgresRepository extends CrudRepository<PositionEntity, Long> {
}
