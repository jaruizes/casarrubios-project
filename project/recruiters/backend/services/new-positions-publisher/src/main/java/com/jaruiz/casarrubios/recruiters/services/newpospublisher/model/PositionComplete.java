package com.jaruiz.casarrubios.recruiters.services.newpospublisher.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PositionComplete {
    private Long id;
    private String title;
    private String description;
    private String status;
    private long createdAt;
    private long publishedAt;
    private String tags;
    private List<PositionRequirement> requirements;
    private List<PositionBenefit> benefits;
    private List<PositionTask> tasks;

    public PositionComplete() {}

    public void addRequirement(PositionRequirement requirement) {
        if (requirements == null) {
            requirements = new ArrayList<>();
        }
        requirements.add(requirement);
    }

    public void addBenefit(PositionBenefit benefit) {
        if (benefits == null) {
            benefits = new ArrayList<>();
        }
        benefits.add(benefit);
    }

    public void addTask(PositionTask task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        tasks.add(task);
    }

    public void addPosition(Position position) {
        setId(position.getId());
        setTitle(position.getTitle());
        setDescription(position.getDescription());
        setStatus(position.getStatus());
        setCreatedAt(position.getCreatedAt());
        setPublishedAt(position.getPublishedAt());
        setTags(position.getTags());
    }
}
