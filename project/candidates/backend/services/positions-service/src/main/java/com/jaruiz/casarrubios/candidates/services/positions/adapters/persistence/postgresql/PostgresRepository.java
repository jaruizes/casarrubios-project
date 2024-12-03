package com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql;

import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.PositionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostgresRepository extends CrudRepository<PositionEntity, Long> {
}
