package com.jaruiz.casarrubios.notifications.services.business.model;

import java.util.Map;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Notification {
    private final UUID id;
    @Setter private String applicationId;
    @Setter private Long positionId;
    @Setter private NotificationType type;
    @Setter private Map<String, Object> data;

    public Notification() {
        this.id = UUID.randomUUID();
    }
}
