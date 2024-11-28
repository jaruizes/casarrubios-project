package com.jaruiz.casarrubios.candidates.services.positionsservice.adapters.output.persistence.postgresql.entities;

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
    private List<RequirementsEntity> requirements;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "position", fetch = FetchType.LAZY)
    private List<ConditionsEntity> conditions;

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

    public List<RequirementsEntity> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<RequirementsEntity> requirements) {
        this.requirements = requirements;
    }

    public List<ConditionsEntity> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionsEntity> conditions) {
        this.conditions = conditions;
    }
}
