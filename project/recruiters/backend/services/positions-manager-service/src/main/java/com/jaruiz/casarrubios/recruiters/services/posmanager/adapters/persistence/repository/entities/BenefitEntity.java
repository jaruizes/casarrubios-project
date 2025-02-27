package com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "BENEFITS")
public class BenefitEntity extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public long id;

    @ManyToOne @JoinColumn(name = "position_id")
    public PositionEntity position;

    public String description;

}
