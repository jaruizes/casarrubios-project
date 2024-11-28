package com.jaruiz.casarrubios.candidates.services.positionsservice.business;

import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positionsservice.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.model.Condition;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.model.Position;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.model.Requirement;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.ports.PersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PositionServiceTest {

    @InjectMocks
    private PositionService positionService;

    @Mock
    private PersistencePort persistencePort;

    @Test
    public void givenAValidPositionId_whenGetDetail_thenPositionIsRetrieved() {
        given(persistencePort.getPositionById(1L)).willReturn(buildPositionFake(true));

        try {
            final Position position = positionService.getPositionDetail(1L);
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

        } catch (PositionNotFoundException e) {
            fail("Position not found");
        }

    }

    @Test
    public void givenAnInvalidPositionId_whenGetDetail_thenPositionNotFoundExceptionIsThrown() {
        given(persistencePort.getPositionById(1L)).willReturn(null);

        try {
            positionService.getPositionDetail(1L);
            fail("Position not found");
        } catch (PositionNotFoundException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void givenNoPositions_whenGetPositions_thenEmptyListIsReturned() {
        given(persistencePort.getAllPositions()).willReturn(List.of());

        final List<Position> positions = positionService.getPositions();
        assertNotNull(positions);
        assertTrue(positions.isEmpty());
    }

    @Test
    public void givenSomePositions_whenGetPositions_thenListIsReturned() {
        given(persistencePort.getAllPositions()).willReturn(List.of(buildPositionFake(false), buildPositionFake(false)));

        final List<Position> positions = positionService.getPositions();
        assertNotNull(positions);
        assertFalse(positions.isEmpty());
        assertEquals(2, positions.size());

        positions.forEach(position -> {
            assertNotNull(position);
            assertNotNull(position.getTitle());
            assertNotNull(position.getDescription());
            assertTrue(position.getRequirements().isEmpty());
            assertTrue(position.getConditions().isEmpty());
        });
    }


    private Position buildPositionFake(boolean withRequirementsAndConditions) {
        final Requirement requirementFake = new Requirement(1L, "Description de prueba");
        final Condition conditionFake = new Condition(1L, "Description de prueba");

        if(withRequirementsAndConditions) {
            return new Position(1L, "Title", "Description", List.of(requirementFake), List.of(conditionFake));
        }

        return new Position(1L, "Title", "Description", List.of(), List.of());
    }

}
