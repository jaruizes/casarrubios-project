package com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.kafka;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.Position;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@Getter @ApplicationScoped
public class PositionCompleteConsumer {

    private Position positionComplete;

    @Blocking
    @Incoming("position-published")
    public void positionPublishedEventHandler(Position positionComplete) {
        this.positionComplete = positionComplete;
    }

    public boolean isPositionCompletePublished() {
        return positionComplete != null;
    }

    public void reset() {
        positionComplete = null;
    }
}
