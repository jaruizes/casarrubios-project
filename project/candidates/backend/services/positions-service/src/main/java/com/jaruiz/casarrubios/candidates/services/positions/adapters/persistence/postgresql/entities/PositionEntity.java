package com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "position", fetch = FetchType.LAZY)
    private List<RequirementEntity> requirements;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "position", fetch = FetchType.LAZY)
    private List<ConditionEntity> conditions;

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

    public List<ConditionEntity> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionEntity> conditions) {
        this.conditions = conditions;
    }
}
