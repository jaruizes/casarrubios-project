package com.jaruiz.casarrubios.notifications.services.business;

import java.util.Map;

import com.jaruiz.casarrubios.notifications.services.business.model.ApplicationScore;
import com.jaruiz.casarrubios.notifications.services.business.model.Notification;
import com.jaruiz.casarrubios.notifications.services.business.model.NotificationType;
import com.jaruiz.casarrubios.notifications.services.business.ports.NotificationPublisher;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class NotificationsManagerService {
    private static final Logger logger = Logger.getLogger(NotificationsManagerService.class);

    private NotificationPublisher notificationPublisher;

    public NotificationsManagerService(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    public void evaluateApplicationScore(ApplicationScore applicationScore) {
        if (applicationScore.getScore().doubleValue() > 0.6) {
            logger.info("Application (" + applicationScore.getApplicationId() + ") scored above 60.0");
            final Map<String, Object> data = Map.of("score", applicationScore.getScore());

            final Notification notification = new Notification();
            notification.setApplicationId(applicationScore.getApplicationId());
            notification.setPositionId(applicationScore.getPositionId());
            notification.setType(NotificationType.GOOD_SCORING);
            notification.setData(data);

            this.notificationPublisher.publish(notification);
        }
    }
}
