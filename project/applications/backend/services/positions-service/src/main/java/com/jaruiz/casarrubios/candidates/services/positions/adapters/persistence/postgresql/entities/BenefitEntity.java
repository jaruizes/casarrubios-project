package com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "BENEFITS")
public class BenefitEntity {

    @Id
    private long id;

    @ManyToOne @JoinColumn(name = "position_id")
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

    public long getPositionId() {
        return position.getId();
    }

    public PositionEntity getPosition() {
        return position;
    }

    public void setPosition(PositionEntity position) {
        this.position = position;
    }
}
