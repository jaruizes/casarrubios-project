package com.jaruiz.casarrubios.recruiters.services.newpospublisher.serdes;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.*;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class PositionSerdes {

    public static final Serde<Position> positionSerde = Serdes.serdeFrom(new ObjectMapperSerializer<>(), new ObjectMapperDeserializer<>(Position.class));
    public static final Serde<PositionRequirement> positionRequirementSerde = Serdes.serdeFrom(new ObjectMapperSerializer<>(), new ObjectMapperDeserializer<>(PositionRequirement.class));
    public static final Serde<PositionBenefit> positionBenefitSerde = Serdes.serdeFrom(new ObjectMapperSerializer<>(), new ObjectMapperDeserializer<>(PositionBenefit.class));
    public static final Serde<PositionTask> positionTaskSerde = Serdes.serdeFrom(new ObjectMapperSerializer<>(), new ObjectMapperDeserializer<>(PositionTask.class));
    public static final Serde<PositionComplete> positionCompleteSerde = Serdes.serdeFrom(new ObjectMapperSerializer<>(), new ObjectMapperDeserializer<>(PositionComplete.class));
    public static final Serde<PositionKey> positionKeySerde = Serdes.serdeFrom(new ObjectMapperSerializer<>(), new ObjectMapperDeserializer<>(PositionKey.class));
}
