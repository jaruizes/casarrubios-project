package com.jaruiz.casarrubios.candidates.services.positionsservice.adapters.api;

import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positionsservice.adapters.api.rest.PositionsRestController;
import com.jaruiz.casarrubios.candidates.services.positionsservice.adapters.api.rest.dto.PositionDTO;
import com.jaruiz.casarrubios.candidates.services.positionsservice.adapters.api.rest.dto.PositionDetailDTO;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.model.Condition;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.model.Position;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.model.Requirement;
import com.jaruiz.casarrubios.candidates.services.positionsservice.business.ports.PositionServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
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

        assertNotNull(positionDTO);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertTrue(positionDTO.getId() != null && positionDTO.getId() > 0);
        assertTrue(positionDTO.getTitle() != null && !positionDTO.getTitle().isEmpty());
        assertTrue(positionDTO.getDescription() != null && !positionDTO.getDescription().isEmpty());
        assertTrue(positionDTO.getRequirements() != null && !positionDTO.getRequirements().isEmpty());
        assertTrue(positionDTO.getConditions() != null && !positionDTO.getConditions().isEmpty());

        positionDTO.getRequirements().forEach(requirement -> {
            assertTrue(requirement.getDescription() != null && !requirement.getDescription().isEmpty());
        });

        positionDTO.getConditions().forEach(condition -> {
            assertTrue(condition.getDescription() != null && !condition.getDescription().isEmpty());
        });
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
        given(positionService.getPositions()).willReturn(List.of());

        final ResponseEntity<List<PositionDTO>> responseEntity = positionsRestController.getAllPositions();
        final List<PositionDTO> positions = responseEntity.getBody();

        assertNotNull(positions);
        assertTrue(positions.isEmpty());
        assertEquals(200, responseEntity.getStatusCode().value());
    }

    @Test
    public void givenSomePositions_whenGetAllPositions_thenListOfPositionsIsReturned() {
        given(positionService.getPositions()).willReturn(List.of(buildPositionFake(false), buildPositionFake(false)));

        final ResponseEntity<List<PositionDTO>> responseEntity = positionsRestController.getAllPositions();
        final List<PositionDTO> positions = responseEntity.getBody();

        assertNotNull(positions);
        assertFalse(positions.isEmpty());
        assertEquals(200, responseEntity.getStatusCode().value());

        positions.forEach(position -> {
            assertTrue(position.getId() != null && position.getId() > 0);
            assertTrue(position.getTitle() != null && !position.getTitle().isEmpty());
            assertTrue(position.getDescription() != null && !position.getDescription().isEmpty());
        });
    }

    @Test
    public void givenANullPositionId_whenGetPositionDetail_thenError400IsReceived() {
        final ResponseEntity<PositionDetailDTO> responseEntity = positionsRestController.getPositionDetail(null);

        assertNotNull(responseEntity);
        assertEquals(400, responseEntity.getStatusCode().value());
    }


    private Position buildPositionFake(boolean withRequirementsAndConditions) {
        final Requirement requirementFake = new Requirement(1L, "Description de prueba");
        final Condition conditionFake = new Condition(1L, "Description de prueba");

        if(withRequirementsAndConditions) {
            return new Position(1L, "Title", "Description", List.of(requirementFake), List.of(conditionFake));
        }

        return new Position(1L, "Title", "Description", null, null);
    }

}
