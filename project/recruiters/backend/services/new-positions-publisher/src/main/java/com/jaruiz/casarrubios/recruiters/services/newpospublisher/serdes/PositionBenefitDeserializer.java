package com.jaruiz.casarrubios.recruiters.services.newpospublisher.serdes;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.PositionBenefit;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class PositionBenefitDeserializer extends JsonbDeserializer<PositionBenefit> {
    public PositionBenefitDeserializer() {
        super(PositionBenefit.class);
    }
}
