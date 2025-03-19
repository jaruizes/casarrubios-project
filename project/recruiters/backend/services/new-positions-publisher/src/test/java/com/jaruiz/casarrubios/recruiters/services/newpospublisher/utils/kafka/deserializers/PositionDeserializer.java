package com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.kafka.deserializers;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.Position;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class PositionDeserializer extends ObjectMapperDeserializer<Position> {
    public PositionDeserializer() {
        super(Position.class);
    }
}
