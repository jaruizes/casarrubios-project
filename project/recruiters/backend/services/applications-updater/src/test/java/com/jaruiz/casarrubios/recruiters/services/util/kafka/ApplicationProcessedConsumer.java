package com.jaruiz.casarrubios.recruiters.services.util.kafka;

import com.jaruiz.casarrubios.recruiters.services.adapters.api.async.dto.ApplicationDTO;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class ApplicationProcessedConsumer {

    private ApplicationDTO applicationProcessedDTO;

    @Blocking
    @Incoming("applications-processed-test")
    public void applicationProcessed(ApplicationDTO applicationProcessedDTO) {
        this.applicationProcessedDTO = applicationProcessedDTO;
    }

    public boolean isApplicationProcessedEventPublished() {
        return applicationProcessedDTO != null;
    }

    public ApplicationDTO getApplicationProcessedDTO() {
        return applicationProcessedDTO;
    }
}
