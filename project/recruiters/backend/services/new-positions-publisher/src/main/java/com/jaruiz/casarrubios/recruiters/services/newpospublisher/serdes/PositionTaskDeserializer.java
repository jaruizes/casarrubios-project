package com.jaruiz.casarrubios.recruiters.services.newpospublisher.serdes;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.PositionTask;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class PositionTaskDeserializer extends JsonbDeserializer<PositionTask> {
    public PositionTaskDeserializer() {
        super(PositionTask.class);
    }
}
