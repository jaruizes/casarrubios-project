package com.jaruiz.casarrubios.recruiters.services.posmanager.adapters.persistence.repository.entities;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "POSITIONS")
public class PositionEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String title;
    public String description;
    public int status;
    public String tags;
    @Column(name = "created_at")
    public LocalDateTime createdAt;
    @Column(name = "published_at")
    public LocalDateTime publishedAt;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public List<RequirementEntity> requirements;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public List<BenefitEntity> benefits;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public List<TaskEntity> tasks;
}
