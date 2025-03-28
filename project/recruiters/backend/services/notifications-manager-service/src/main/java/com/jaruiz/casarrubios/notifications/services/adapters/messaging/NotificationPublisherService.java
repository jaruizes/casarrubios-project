package com.jaruiz.casarrubios.notifications.services.adapters.messaging;

import com.jaruiz.casarrubios.notifications.services.adapters.messaging.dto.NotificationDTO;
import com.jaruiz.casarrubios.notifications.services.business.model.Notification;
import com.jaruiz.casarrubios.notifications.services.business.ports.NotificationPublisher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class NotificationPublisherService implements NotificationPublisher {
    private static final Logger logger = Logger.getLogger(NotificationPublisherService.class);

    @Inject
    @Channel("notifications-publisher") Emitter<NotificationDTO> notificationDTOEmitter;

    @Override public void publish(Notification notification) {
        logger.info("Publishing notification for application (" + notification.getApplicationId() + ")");

        NotificationDTO notificationDTO = mapToNotificationDTO(notification);
        notificationDTOEmitter.send(notificationDTO).whenComplete(
            (success, error) -> {
                if (error != null) {
                    logger.error("Error publishing notification for application (" + notification.getApplicationId() + ")", error);
                }

                logger.info("Notification for application (" + notification.getApplicationId() + ") published");
            }
        );
    }

    private NotificationDTO mapToNotificationDTO(Notification notification) {
        final NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notification.getId().toString());
        notificationDTO.setApplicationId(notification.getApplicationId());
        notificationDTO.setPositionId(notification.getPositionId());
        notificationDTO.setType(notification.getType().toString());
        notificationDTO.setData(notification.getData());

        return notificationDTO;
    }
}
