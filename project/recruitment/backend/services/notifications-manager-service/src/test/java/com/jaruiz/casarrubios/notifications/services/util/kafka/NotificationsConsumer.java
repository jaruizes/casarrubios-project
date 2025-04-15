package com.jaruiz.casarrubios.notifications.services.util.kafka;

import java.util.ArrayList;
import java.util.List;

import com.jaruiz.casarrubios.notifications.services.adapters.messaging.dto.NotificationDTO;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@Getter
@ApplicationScoped
public class NotificationsConsumer {

    private List<NotificationDTO> notificationsDTO;

    public NotificationsConsumer() {
        this.notificationsDTO = new ArrayList<>();
    }

    @Blocking
    @Incoming("notifications-in")
    public void notificationsHandler(NotificationDTO notificationDTO) {
        this.notificationsDTO.add(notificationDTO);
    }

    public boolean isNotificationPublished() {
        return !notificationsDTO.isEmpty();
    }

}
