package com.jaruiz.casarrubios.candidates.services.positions.business.model;

import java.util.List;

public class Position {

    private final long id;
    private final String title;
    private final String description;
    private final List<Requirement> requirements;
    private final List<Condition> conditions;

    public Position(long id, String title, String description, List<Requirement> requirements, List<Condition> conditions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.conditions = conditions;
    }

    public long getId() {
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
}
