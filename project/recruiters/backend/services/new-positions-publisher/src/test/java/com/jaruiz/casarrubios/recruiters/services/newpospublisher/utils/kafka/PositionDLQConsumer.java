package com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.kafka;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.Position;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@Getter @ApplicationScoped
public class PositionDLQConsumer {

    private Position positionDLQ;

    @Blocking
    @Incoming("positions-published-dlq")
    public void positionPublishedDQLEventHandler(Position positionDLQ) {
        this.positionDLQ = positionDLQ;
    }

    public boolean isPositionDQL() {
        return positionDLQ != null;
    }
}
