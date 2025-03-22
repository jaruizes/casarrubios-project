package com.jaruiz.casarrubios.recruiters.services.newpospublisher.serdes;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.PositionRequirement;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class PositionRequirementDeserializer extends JsonbDeserializer<PositionRequirement> {
    public PositionRequirementDeserializer() {
        super(PositionRequirement.class);
    }
}
