package com.jaruiz.casarrubios.recruiters.services.posmanager.business.model;

import java.time.LocalDateTime;
import java.util.List;

public class Position {

    private Long id;
    private final PositionData data;
    private PositionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;

    public Position(PositionData data) {
        this.data = data;
        this.createdAt = LocalDateTime.now();
        this.status = PositionStatus.DRAFT;
    }

    public Position(long id, PositionData data, LocalDateTime createdAt, LocalDateTime publishedAt) {
        this(data);
        this.id = id;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
    }

    public PositionData getData() {
        return data;
    }

    public Long getId() {
        return id;
    }

    public PositionStatus getStatus() {
        return status;
    }

    public String getTags() {
        return getData().getTags();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public String getTitle() {
        return data.getTitle();
    }

    public String getDescription() {
        return data.getDescription();
    }

    public List<Requirement> getRequirements() {
        return data.getRequirements();
    }

    public List<Benefit> getBenefits() {
        return data.getBenefits();
    }

    public List<Task> getTasks() {
        return data.getTasks();
    }

    public void setStatus(PositionStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}
