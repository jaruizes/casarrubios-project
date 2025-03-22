package com.jaruiz.casarrubios.recruiters.services.newpospublisher.serdes;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.Position;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class PositionDeserializer extends JsonbDeserializer<Position> {
    public PositionDeserializer() {
        super(Position.class);
    }
}
