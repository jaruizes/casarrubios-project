package com.jaruiz.casarrubios.candidates.services.positions.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.ConditionEntity;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.PositionEntity;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.RequirementEntity;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.TaskEntity;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.*;

public final class FakeUtils {

    private FakeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Position buildPositionFake(boolean withRequirementsAndConditions) {
        final Requirement requirementFake = new Requirement("Key", "Value", "Description de prueba", true);
        final Condition conditionFake = new Condition(1L, "Description de prueba");
        final Task taskFake = new Task(1L, "Description de prueba");

        if(withRequirementsAndConditions) {
            return new Position(1L, "Title", "Description", "Java, Python", new Date(), 10, List.of(requirementFake), List.of(conditionFake), List.of(taskFake));
        }

        return new Position(1L, "Title", "Description", "Java, Python", new Date(), 10, null, null, null);
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
        requirementFake.setKey("Key");
        requirementFake.setValue("Value");
        requirementFake.setDescription("Description");
        requirementFake.setMandatory(true);

        final ConditionEntity conditionFake = new ConditionEntity();
        conditionFake.setId(1L);
        conditionFake.setDescription("Description");

        final TaskEntity taskFake = new TaskEntity();
        taskFake.setId(1L);
        taskFake.setDescription("Description");

        PositionEntity positionEntity = new PositionEntity();
        positionEntity.setId(1L);
        positionEntity.setTitle("Title");
        positionEntity.setDescription("Description");
        positionEntity.setTags("Java, Python");
        positionEntity.setCreatedAt(new Date());
        positionEntity.setApplications(0);
        positionEntity.setRequirements(List.of(requirementFake));
        positionEntity.setConditions(List.of(conditionFake));
        positionEntity.setTasks(List.of(taskFake));

        return positionEntity;
    }
}
