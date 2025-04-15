package com.jaruiz.casarrubios.notifications.services.adapters.messaging.dto;

import java.util.Map;
import java.util.UUID;

import com.jaruiz.casarrubios.notifications.services.business.model.NotificationType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationDTO {
    private String id;
    private String applicationId;
    private Long positionId;
    private String type;
    private Map<String, Object> data;
}
