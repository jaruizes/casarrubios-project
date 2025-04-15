package com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository;

import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.adapters.outbox.repository.entitites.OutboxEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OutboxRepository implements PanacheRepositoryBase<OutboxEntity, UUID> {

}
