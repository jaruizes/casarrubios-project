package com.jaruiz.casarrubios.candidates.services.positions.utils;

import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest.dto.PaginatedPositionsDTO;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest.dto.PositionDTO;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest.dto.PositionDetailDTO;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Position;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.PositionsList;
import static org.junit.jupiter.api.Assertions.*;

public final class AssertUtils {
    public static void assertPaginatedPosition(long total, int page, int pageSize, PaginatedPositionsDTO paginatedPositionsDTO) {
        int totalPages = total > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
        assertNotNull(paginatedPositionsDTO);
        assertEquals(page, paginatedPositionsDTO.getNumber());
        assertEquals(pageSize, paginatedPositionsDTO.getSize());
        assertEquals(total, paginatedPositionsDTO.getTotalElements());
        assertEquals(totalPages, paginatedPositionsDTO.getTotalPages());

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
                assertTrue(position.getTags() != null && !position.getTags().isEmpty());
                assertTrue(position.getCreatedAt() != null && !position.getCreatedAt().isEmpty());
                assertNotNull(position.getApplications());
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
            positions.getPositions().forEach(AssertUtils::assertPosition);
        }
    }

    public static void assertPosition(Position position) {
        assertNotNull(position);
        assertTrue(position.getId() > 0);
        assertTrue(position.getTitle() != null && !position.getTitle().isEmpty());
        assertTrue(position.getDescription() != null && !position.getDescription().isEmpty());
        assertTrue(position.getTags() != null && !position.getTags().isEmpty());
        assertNotNull(position.getCreatedAt());
        assertTrue(position.getRequirements() != null && !position.getRequirements().isEmpty());
        assertTrue(position.getConditions() != null && !position.getConditions().isEmpty());
        assertTrue(position.getTasks() != null && !position.getTasks().isEmpty());

        position.getRequirements().forEach(requirement -> {
            assertTrue(requirement.getDescription() != null && !requirement.getDescription().isEmpty());
            assertTrue(requirement.getKey() != null  && !requirement.getKey().isEmpty());
            assertTrue(requirement.getValue() != null  && !requirement.getValue().isEmpty());
            assertNotNull(requirement.getMandatory());
        });

        position.getConditions().forEach(condition -> {
            assertTrue(condition.getId() > 0);
            assertTrue(condition.getDescription() != null && !condition.getDescription().isEmpty());
        });

        position.getTasks().forEach(task -> {
            assertTrue(task.getId() > 0);
            assertTrue(task.getDescription() != null && !task.getDescription().isEmpty());
        });
    }

    public static void assertPositionDetailDTO(PositionDetailDTO positionDTO) {
        assertNotNull(positionDTO);
        assertTrue(positionDTO.getId() != null && positionDTO.getId() > 0);
        assertTrue(positionDTO.getTitle() != null && !positionDTO.getTitle().isEmpty());
        assertTrue(positionDTO.getDescription() != null && !positionDTO.getDescription().isEmpty());
        assertTrue(positionDTO.getTags() != null && !positionDTO.getTags().isEmpty());
        assertTrue(positionDTO.getCreatedAt() != null && !positionDTO.getCreatedAt().isEmpty());
        assertTrue(positionDTO.getRequirements() != null && !positionDTO.getRequirements().isEmpty());
        assertTrue(positionDTO.getConditions() != null && !positionDTO.getConditions().isEmpty());
        assertTrue(positionDTO.getTasks() != null && !positionDTO.getTasks().isEmpty());

        positionDTO.getRequirements().forEach(requirement -> {
            assertTrue(requirement.getKey() != null && !requirement.getKey().isEmpty());
            assertTrue(requirement.getValue() != null && !requirement.getValue().isEmpty());
            assertTrue(requirement.getDescription() != null && !requirement.getDescription().isEmpty());
            assertNotNull(requirement.isMandatory());
        });

        positionDTO.getConditions().forEach(condition -> {
            assertTrue(condition.getDescription() != null && !condition.getDescription().isEmpty());
        });

        positionDTO.getTasks().forEach(task -> {
            assertTrue(task.getDescription() != null && !task.getDescription().isEmpty());
        });
    }
}
