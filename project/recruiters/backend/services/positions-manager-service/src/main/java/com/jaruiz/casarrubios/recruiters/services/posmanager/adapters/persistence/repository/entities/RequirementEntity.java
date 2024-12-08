package com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "REQUIREMENTS")
public class RequirementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="position_id")
    private PositionEntity position;

    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPositionId() {
        return position.getId();
    }

    public PositionEntity getPosition() {
        return position;
    }

    public void setPosition(PositionEntity position) {
        this.position = position;
    }
}
