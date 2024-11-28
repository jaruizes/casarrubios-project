package com.jaruiz.casarrubios.candidates.services.positionsservice.adapters.output.persistence.postgresql;

import com.jaruiz.casarrubios.candidates.services.positionsservice.adapters.output.persistence.postgresql.entities.PositionEntity;
import org.springframework.data.repository.CrudRepository;

public interface PostgresRepository extends CrudRepository<PositionEntity, Long> {
}
