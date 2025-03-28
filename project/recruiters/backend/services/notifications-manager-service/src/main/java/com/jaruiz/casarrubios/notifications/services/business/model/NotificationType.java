package com.jaruiz.casarrubios.notifications.services.business.model;

import lombok.Getter;

@Getter public enum NotificationType {
    GOOD_SCORING("good_scoring");

    private final String type;

    NotificationType(String type) {
        this.type = type;
    }

}
