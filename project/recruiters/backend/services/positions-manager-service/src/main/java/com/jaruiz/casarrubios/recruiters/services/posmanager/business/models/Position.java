package com.jaruiz.casarrubios.recruiters.services.posmanager.business.models;

import java.util.List;

public class Position {

    private final Long id;
    private final String title;
    private final String description;
    private final List<Requirement> requirements;
    private final List<Condition> conditions;

    public Position(Long id, String title, String description, List<Requirement> requirements, List<Condition> conditions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.conditions = conditions;
    }

    public boolean isValid() {
        return (title != null && !title.isEmpty()) &&
            (description != null && !description.isEmpty()) &&
            areRequirementsValid() &&
            areConditionsValid();
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

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    private boolean areRequirementsValid() {
        if (requirements != null && !requirements.isEmpty()) {
            return requirements.stream().allMatch(Requirement::isValid);
        }

        return false;
    }

    private boolean areConditionsValid() {
        if (conditions != null && !conditions.isEmpty()) {
            return conditions.stream().allMatch(Condition::isValid);
        }

        return false;
    }
}
