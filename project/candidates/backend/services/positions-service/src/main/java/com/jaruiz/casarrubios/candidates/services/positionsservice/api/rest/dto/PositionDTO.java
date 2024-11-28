package com.jaruiz.casarrubios.candidates.services.positionsservice.api.rest.dto;

import java.io.Serializable;
import java.util.List;

public class PositionDTO implements Serializable {

    private final Long id;
    private final String title;
    private final String description;
    private final List<RequirementDTO> requirements;
    private final List<ConditionDTO> conditions;

    public PositionDTO(Long id, String title, String description, List<RequirementDTO> requirements, List<ConditionDTO> conditions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.conditions = conditions;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<RequirementDTO> getRequirements() {
        return requirements;
    }

    public List<ConditionDTO> getConditions() {
        return conditions;
    }
}
