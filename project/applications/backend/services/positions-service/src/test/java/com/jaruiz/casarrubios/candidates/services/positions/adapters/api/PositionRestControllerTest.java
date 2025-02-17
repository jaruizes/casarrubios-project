package com.jaruiz.casarrubios.candidates.services.positions.adapters.api;

import java.util.ArrayList;
import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest.PositionsRestController;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest.dto.PaginatedPositionsDTO;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest.dto.PositionDTO;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest.dto.PositionDetailDTO;
import com.jaruiz.casarrubios.candidates.services.positions.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Condition;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Position;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.PositionsList;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Requirement;
import com.jaruiz.casarrubios.candidates.services.positions.business.ports.PositionServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import static com.jaruiz.casarrubios.candidates.services.positions.utils.AssertUtils.assertPaginatedPosition;
import static com.jaruiz.casarrubios.candidates.services.positions.utils.AssertUtils.assertPositionDetailDTO;
import static com.jaruiz.casarrubios.candidates.services.positions.utils.FakeUtils.buildPositionFake;
import static com.jaruiz.casarrubios.candidates.services.positions.utils.FakeUtils.buildPositionsListFake;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PositionRestControllerTest {

    @Mock
    private PositionServicePort positionService;

    @InjectMocks
    private PositionsRestController positionsRestController;

    @Test
    public void givenAValidPositionId_whenGetPositionDetail_thenPositionIsRetrieved() {
        try {
            given(positionService.getPositionDetail(1L)).willReturn(buildPositionFake(true));
        } catch (PositionNotFoundException e) {
            fail();
        }

        final ResponseEntity<PositionDetailDTO> responseEntity = positionsRestController.getPositionDetail(1L);
        final PositionDetailDTO positionDTO = responseEntity.getBody();

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertPositionDetailDTO(positionDTO);
    }

    @Test
    public void givenAnInvalidPositionId_whenGetPositionDetail_then404IsReceived() {
        try {
            given(positionService.getPositionDetail(1L)).willThrow(PositionNotFoundException.class);
        } catch (PositionNotFoundException e) {
            fail();
        }

        final ResponseEntity<PositionDetailDTO> responseEntity = positionsRestController.getPositionDetail(1L);
        final PositionDetailDTO positionDTO = responseEntity.getBody();

        assertNull(positionDTO);
        assertEquals(404, responseEntity.getStatusCode().value());
    }

    @Test
    public void givenNoPositions_whenGetAllPositions_thenEmptyListIsReturned() {
        var total = 0L;
        given(positionService.getPositions(1, 10)).willReturn(buildPositionsListFake(1,0,total));

        final ResponseEntity<PaginatedPositionsDTO> responseEntity = positionsRestController.getAllPositions(1, 10);
        final PaginatedPositionsDTO paginatedPositionsDTO = responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCode().value());
        assertPaginatedPosition(total, 1, 10, paginatedPositionsDTO);
    }

    @Test
    public void givenSomePositions_whenGetAllPositions_thenListOfPositionsIsReturned() {
        var total = 100L;
        given(positionService.getPositions(1, 10)).willReturn(buildPositionsListFake(1,10,total));

        final ResponseEntity<PaginatedPositionsDTO> responseEntity = positionsRestController.getAllPositions(1, 10);
        final PaginatedPositionsDTO paginatedPositionsDTO = responseEntity.getBody();
        assertEquals(200, responseEntity.getStatusCode().value());
        assertPaginatedPosition(total, 1, 10, paginatedPositionsDTO);
    }

    @Test
    public void givenANullPositionId_whenGetPositionDetail_thenError400IsReceived() {
        final ResponseEntity<PositionDetailDTO> responseEntity = positionsRestController.getPositionDetail(null);

        assertNotNull(responseEntity);
        assertEquals(400, responseEntity.getStatusCode().value());
    }

}
