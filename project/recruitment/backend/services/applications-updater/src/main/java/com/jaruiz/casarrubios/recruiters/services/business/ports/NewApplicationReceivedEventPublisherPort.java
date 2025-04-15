package com.jaruiz.casarrubios.recruiters.services.business.ports;

import com.jaruiz.casarrubios.recruiters.services.business.model.Application;

public interface NewApplicationReceivedEventPublisherPort {
    static final String NEW_APPLICATION_RECEIVED_EVENT = "NewApplicationReceivedEvent";
    static final String NEW_APPLICATION_RECEIVED_EVENT_TYPE = "Application";

    void publishNewApplicationReceivedEvent(Application application);
}
