package com.jaruiz.casarrubios.recruiters.services.applications.adapters.positions.persistence.postgresql.entities;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "POSITIONS")
public class PositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    private String description;
    private String tags;
    private Date createdAt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "position", fetch = FetchType.EAGER)
    private List<RequirementEntity> requirements;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "position", fetch = FetchType.EAGER)
    private List<BenefitEntity> benefits;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "position", fetch = FetchType.EAGER)
    private List<TaskEntity> tasks;

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

    public List<RequirementEntity> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<RequirementEntity> requirements) {
        this.requirements = requirements;
    }

    public List<BenefitEntity> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<BenefitEntity> benefits) {
        this.benefits = benefits;
    }

    public List<TaskEntity> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskEntity> tasks) {
        this.tasks = tasks;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
