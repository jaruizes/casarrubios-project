package com.jaruiz.casarrubios.recruiters.services.applications.adapters.positions;

import com.jaruiz.casarrubios.recruiters.services.applications.adapters.positions.persistence.postgresql.PostgresRepository;
import com.jaruiz.casarrubios.recruiters.services.applications.adapters.positions.persistence.postgresql.util.PositionMapper;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Position;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.PositionsServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PositionServiceAdapter implements PositionsServicePort {
    private static final Logger logger = LoggerFactory.getLogger(PositionServiceAdapter.class);
    private final PostgresRepository postgresRepository;

    public PositionServiceAdapter(PostgresRepository postgresRepository) {
        this.postgresRepository = postgresRepository;
    }

    public Position getPositionById(long positionId) {
        logger.debug("Getting position detail for position with id {}", positionId);
        final Position position = this.postgresRepository.findById(positionId)
                                                         .map(PositionMapper::positionEntityToPosition)
                                                         .orElseGet(() -> {
                                                             logger.error("Position with id {} not found in the database", positionId);
                                                             return null;
                                                         });

        logger.debug("Position with id {} found in the database", positionId);
        return position;
    }
}
