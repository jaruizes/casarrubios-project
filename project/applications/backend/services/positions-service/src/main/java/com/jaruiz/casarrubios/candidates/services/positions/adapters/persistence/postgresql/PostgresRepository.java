package com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql;

import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.PositionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PostgresRepository extends JpaRepository<PositionEntity, Long> {
    @NonNull
    Page<PositionEntity> findAll(@NonNull Pageable pageable);
}
