package com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "POSITIONS_REQUIREMENTS")
public class RequirementEntity {

    @Id
    private long id;

    @ManyToOne @JoinColumn(name = "position_id")
    private PositionEntity position;

    private String key;
    private String value;
    private Boolean mandatory;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }
}
