package com.jaruiz.casarrubios.candidates.services.positions.business.model;

import java.util.Date;
import java.util.List;

public class Position {

    private final long id;
    private final String title;
    private final String description;
    private final String tags;
    private final Date createdAt;
    private int applications;
    private final List<Requirement> requirements;
    private final List<Condition> conditions;
    private final List<Task> tasks;

    public Position(long id, String title, String description, String tags, Date createdAt, int applications, List<Requirement> requirements, List<Condition> conditions, List<Task> tasks) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.createdAt = createdAt;
        this.applications = applications;
        this.requirements = requirements;
        this.conditions = conditions;
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

    public List<Condition> getConditions() {
        return conditions;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public String getTags() {
        return tags;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public int getApplications() {
        return applications;
    }

    public void setApplications(int applications) {
        this.applications = applications;
    }
}
