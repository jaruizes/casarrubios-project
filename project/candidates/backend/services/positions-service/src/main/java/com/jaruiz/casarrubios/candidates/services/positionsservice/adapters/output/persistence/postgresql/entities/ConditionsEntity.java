package com.jaruiz.casarrubios.candidates.services.positionsservice.adapters.output.persistence.postgresql.entities;

import com.jaruiz.casarrubios.candidates.services.positionsservice.business.model.Position;
import jakarta.persistence.*;

@Entity
@Table(name = "CONDITIONS")
public class ConditionsEntity {

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
