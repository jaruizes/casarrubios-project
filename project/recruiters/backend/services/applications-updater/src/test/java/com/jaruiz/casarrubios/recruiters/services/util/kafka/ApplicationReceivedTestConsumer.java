package com.jaruiz.casarrubios.recruiters.services.util.kafka;

import com.jaruiz.casarrubios.recruiters.services.api.output.async.dto.NewApplicationReceivedDTO;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class ApplicationReceivedTestConsumer {

    private NewApplicationReceivedDTO newApplicationReceivedDTO;

    @Blocking
    @Incoming("applications-received")
    public void applicationsReceivedEventHandler(NewApplicationReceivedDTO newApplicationReceivedDTO) {
        this.newApplicationReceivedDTO = newApplicationReceivedDTO;
    }

    public boolean isApplicationReceivedEventPublished() {
        return newApplicationReceivedDTO != null;
    }

    public NewApplicationReceivedDTO getApplicationReceivedDTO() {
        return newApplicationReceivedDTO;
    }
}
