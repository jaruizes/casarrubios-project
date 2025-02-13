package com.jaruiz.casarrubios.candidates.services.positions.utils;

import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest.dto.PaginatedPositionsDTO;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest.dto.PositionDTO;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.PositionsList;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class AssertUtils {
    public static void assertPaginatedPosition(long total, int page, int pageSize, PaginatedPositionsDTO paginatedPositionsDTO) {
        assertNotNull(paginatedPositionsDTO);
        assertEquals(page, paginatedPositionsDTO.getNumber());
        assertEquals(pageSize, paginatedPositionsDTO.getSize());
        assertEquals(total, paginatedPositionsDTO.getTotalElements());

        final List<PositionDTO> positions = paginatedPositionsDTO.getContent();
        assertNotNull(paginatedPositionsDTO.getContent());

        if (total == 0) {
            assertTrue(positions.isEmpty());
        }

        if (total > 0) {
            assertFalse(positions.isEmpty());
            positions.forEach(position -> {
                assertTrue(position.getId() != null && position.getId() > 0);
                assertTrue(position.getTitle() != null && !position.getTitle().isEmpty());
                assertTrue(position.getDescription() != null && !position.getDescription().isEmpty());
            });
        }
    }

    public static void assertPositionsList(long total, PositionsList positions) {
        assertNotNull(positions);
        assertEquals(1, positions.getPage());
        assertEquals(10, positions.getPageSize());
        assertEquals(total, positions.getTotal());
        assertNotNull(positions.getPositions());

        if (total == 0) {
            assertTrue(positions.getPositions().isEmpty());
        }

        if (total > 0) {
            assertFalse(positions.getPositions().isEmpty());
            positions.getPositions().forEach(position -> {
                assertNotNull(position);
                assertNotNull(position.getTitle());
                assertNotNull(position.getDescription());
                assertFalse(position.getRequirements().isEmpty());
                assertFalse(position.getConditions().isEmpty());
            });
        }
    }
}
