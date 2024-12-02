package com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql;

import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.PositionEntity;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface PostgresRepository extends CrudRepository<PositionEntity, Long> {
}
