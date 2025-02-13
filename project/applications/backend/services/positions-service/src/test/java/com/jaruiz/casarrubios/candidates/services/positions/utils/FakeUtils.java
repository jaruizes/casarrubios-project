package com.jaruiz.casarrubios.candidates.services.positions.utils;

import java.util.ArrayList;
import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.ConditionEntity;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.PositionEntity;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.RequirementEntity;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Condition;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Position;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.PositionsList;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Requirement;

public final class FakeUtils {

    private FakeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Position buildPositionFake(boolean withRequirementsAndConditions) {
        final Requirement requirementFake = new Requirement(1L, "Description de prueba");
        final Condition conditionFake = new Condition(1L, "Description de prueba");

        if(withRequirementsAndConditions) {
            return new Position(1L, "Title", "Description", List.of(requirementFake), List.of(conditionFake));
        }

        return new Position(1L, "Title", "Description", null, null);
    }

    public static PositionsList buildPositionsListFake(int page, int size, long total) {
        final List<Position> positions = new ArrayList<>();
        if (total > 0) {
            for (int i = 0; i < size; i++) {
                positions.add(buildPositionFake(true));
            }
        }

        return new PositionsList(total, page, size, positions);
    }

    public static List<PositionEntity> buildPositionsEntityListFake(int page, int size, long total) {
        final List<PositionEntity> positions = new ArrayList<>();
        if (total > 0) {
            for (int i = 0; i < size; i++) {
                positions.add(buildPositionEntityFake());
            }
        }

        return positions;
    }

    public static  PositionEntity buildPositionEntityFake() {
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
