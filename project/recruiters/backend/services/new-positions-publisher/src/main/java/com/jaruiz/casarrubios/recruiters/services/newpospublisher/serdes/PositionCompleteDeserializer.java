package com.jaruiz.casarrubios.recruiters.services.newpospublisher.serdes;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.PositionComplete;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class PositionCompleteDeserializer extends JsonbDeserializer<PositionComplete> {
    public PositionCompleteDeserializer() {
        super(PositionComplete.class);
    }
}
