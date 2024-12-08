package com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.entities;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "POSITIONS")
public class PositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RequirementEntity> requirements;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ConditionEntity> conditions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public List<ConditionEntity> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionEntity> conditions) {
        this.conditions = conditions;
    }
}
