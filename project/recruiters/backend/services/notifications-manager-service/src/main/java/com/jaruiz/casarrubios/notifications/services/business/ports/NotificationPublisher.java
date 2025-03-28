package com.jaruiz.casarrubios.notifications.services.business.ports;

import com.jaruiz.casarrubios.notifications.services.business.model.Notification;

public interface NotificationPublisher {

    void publish(Notification notification);


}
