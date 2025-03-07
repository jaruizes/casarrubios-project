package com.jaruiz.casarrubios.recruiters.services.applications.business.model;

import java.util.Date;
import java.util.List;

public class Position {

    private final long id;
    private final String title;
    private final String description;
    private final String tags;
    private final Date createdAt;
    private final List<Requirement> requirements;
    private final List<Benefit> benefits;
    private final List<Task> tasks;

    public Position(long id, String title, String description, String tags, Date createdAt, List<Requirement> requirements, List<Benefit> benefits, List<Task> tasks) {
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

    public List<Benefit> getBenefits() {
        return benefits;
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

}
