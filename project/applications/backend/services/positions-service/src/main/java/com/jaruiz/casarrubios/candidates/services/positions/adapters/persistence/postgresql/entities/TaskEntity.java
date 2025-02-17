package com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "TASKS")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="position_id", nullable=false)
    private PositionEntity position;
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PositionEntity getPosition() {
        return position;
    }

    public void setPosition(PositionEntity position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
