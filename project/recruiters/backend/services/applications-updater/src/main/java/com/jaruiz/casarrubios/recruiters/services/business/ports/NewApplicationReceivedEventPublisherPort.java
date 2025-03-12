package com.jaruiz.casarrubios.recruiters.services.business.ports;

import com.jaruiz.casarrubios.recruiters.services.business.model.Application;

public interface NewApplicationReceivedEventPublisherPort {
    void publishNewApplicationReceivedEvent(Application application);
}
