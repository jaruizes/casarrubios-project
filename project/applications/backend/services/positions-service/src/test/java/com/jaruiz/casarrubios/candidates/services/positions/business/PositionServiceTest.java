package com.jaruiz.casarrubios.candidates.services.positions.business;

import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positions.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Condition;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Position;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.PositionsList;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Requirement;
import com.jaruiz.casarrubios.candidates.services.positions.business.ports.PersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.jaruiz.casarrubios.candidates.services.positions.utils.AssertUtils.assertPosition;
import static com.jaruiz.casarrubios.candidates.services.positions.utils.AssertUtils.assertPositionsList;
import static com.jaruiz.casarrubios.candidates.services.positions.utils.FakeUtils.buildPositionFake;
import static com.jaruiz.casarrubios.candidates.services.positions.utils.FakeUtils.buildPositionsListFake;
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
            assertPosition(position);

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
        var total = 0L;
        given(persistencePort.getAllPositions(1, 10)).willReturn(buildPositionsListFake(1, 10, total));

        final PositionsList positions = positionService.getPositions(1, 10);
        assertPositionsList(total, positions);
    }

    @Test
    public void givenSomePositions_whenGetPositions_thenListIsReturned() {
        var total = 100L;
        given(persistencePort.getAllPositions(1, 10)).willReturn(buildPositionsListFake(1, 10, total));

        final PositionsList positions = positionService.getPositions(1, 10);
        assertPositionsList(total, positions);
    }



}
