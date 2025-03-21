package com.jaruiz.casarrubios.candidates.services.positions.business.model;

import java.time.LocalDateTime;
import java.util.List;

public class Position {

    private final long id;
    private final String title;
    private final String description;
    private final String tags;
    private final LocalDateTime createdAt;
    private final List<Requirement> requirements;
    private final List<Benefits> benefits;
    private final List<Task> tasks;

    public Position(long id, String title, String description, String tags, LocalDateTime createdAt, List<Requirement> requirements, List<Benefits> benefits, List<Task> tasks) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.createdAt = createdAt;
        this.requirements = requirements;
        this.benefits = benefits;
        this.tasks = tasks;
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

    public List<Benefits> getConditions() {
        return benefits;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public String getTags() {
        return tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
