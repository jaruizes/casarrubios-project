package com.jaruiz.casarrubios.candidates.services.positions.adapters.api.async.dto;

import java.util.List;

public class NewPositionDTO {
    private long id;
    private String title;
    private String description;
    private String status;
    private long createdAt;
    private long publishedAt;
    private String tags;
    private List<PositionRequirementDTO> requirements;
    private List<PositionBenefitDTO> benefits;
    private List<PositionTaskDTO> tasks;

    public NewPositionDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(long publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<PositionRequirementDTO> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<PositionRequirementDTO> requirements) {
        this.requirements = requirements;
    }

    public List<PositionBenefitDTO> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<PositionBenefitDTO> benefits) {
        this.benefits = benefits;
    }

    public List<PositionTaskDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<PositionTaskDTO> tasks) {
        this.tasks = tasks;
    }
}
