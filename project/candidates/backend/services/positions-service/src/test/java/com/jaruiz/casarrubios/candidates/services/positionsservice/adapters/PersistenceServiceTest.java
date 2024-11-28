package com.jaruiz.casarrubios.candidates.services.positionsservice.adapters;

import java.util.List;
import java.util.Optional;

import com.jaruiz.casarrubios.candidates.services.positionsservice.infrastructure.persistence.PersistenceService;
import com.jaruiz.casarrubios.candidates.services.positionsservice.infrastructure.persistence.postgresql.PostgresRepository;
import com.jaruiz.casarrubios.candidates.services.positionsservice.infrastructure.persistence.postgresql.entities.ConditionEntity;
import com.jaruiz.casarrubios.candidates.services.positionsservice.infrastructure.persistence.postgresql.entities.PositionEntity;
import com.jaruiz.casarrubios.candidates.services.positionsservice.infrastructure.persistence.postgresql.entities.RequirementEntity;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.model.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PersistenceServiceTest {

    @Mock
    private PostgresRepository postgresRepository;

    @InjectMocks
    private PersistenceService persistenceService;

    @Test
    public void givenAValidPositionId_whenGetDetail_thenPositionIsRetrieved() {
        given(postgresRepository.findById(1L)).willReturn(Optional.of(buildPositionFake()));

        final Position position = persistenceService.getPositionById(1L);

        assertNotNull(position);
        assertTrue(position.getId() > 0);
        assertTrue(position.getTitle() != null && !position.getTitle().isEmpty());
        assertTrue(position.getDescription() != null && !position.getDescription().isEmpty());
        assertTrue(position.getRequirements() != null && !position.getRequirements().isEmpty());
        assertTrue(position.getConditions() != null && !position.getConditions().isEmpty());

        position.getRequirements().forEach(requirement -> {
            assertTrue(requirement.getId() > 0);
            assertTrue(requirement.getDescription() != null && !requirement.getDescription().isEmpty());
        });

        position.getConditions().forEach(condition -> {
            assertTrue(condition.getId() > 0);
            assertTrue(condition.getDescription() != null && !condition.getDescription().isEmpty());
        });
    }

    @Test
    public void givenAnInvalidPositionId_whenGetDetail_thenNullIsReturned() {
        given(postgresRepository.findById(1L)).willReturn(Optional.empty());

        final Position position = persistenceService.getPositionById(1L);

        assertNull(position);
    }

    @Test
    public void givenNoPositions_whenGetPositions_thenEmptyListIsReturned() {
        given(postgresRepository.findAll()).willReturn(List.of());

        final List<Position> positions = persistenceService.getAllPositions();

        assertNotNull(positions);
        assertTrue(positions.isEmpty());
    }

    @Test
    public void givenSomePositions_whenGetPositions_thenListIsReturned() {
        given(postgresRepository.findAll()).willReturn(List.of(buildPositionFake()));

        final List<Position> positions = persistenceService.getAllPositions();

        assertNotNull(positions);
        assertFalse(positions.isEmpty());
    }

    private PositionEntity buildPositionFake() {
        final RequirementEntity requirementFake = new RequirementEntity();
        requirementFake.setId(1L);
        requirementFake.setDescription("Description");

        final ConditionEntity conditionFake = new ConditionEntity();
        conditionFake.setId(1L);
        conditionFake.setDescription("Description");

        PositionEntity positionEntity = new PositionEntity();
        positionEntity.setId(1L);
        positionEntity.setTitle("Title");
        positionEntity.setDescription("Description");
        positionEntity.setRequirements(List.of(requirementFake));
        positionEntity.setConditions(List.of(conditionFake));

        return positionEntity;
    }


}
