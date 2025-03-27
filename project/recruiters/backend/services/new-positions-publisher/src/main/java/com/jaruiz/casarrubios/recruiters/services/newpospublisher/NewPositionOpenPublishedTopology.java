package com.jaruiz.casarrubios.recruiters.services.newpospublisher;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.config.TopologyConfig;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.PositionTransactionElement;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.processors.TimedPositionAggregatorProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.Stores;
import org.jboss.logging.Logger;
import static com.jaruiz.casarrubios.recruiters.services.newpospublisher.processors.TimedPositionAggregatorProcessor.POSITION_COMPLETE_STORE;
import static com.jaruiz.casarrubios.recruiters.services.newpospublisher.processors.TimedPositionAggregatorProcessor.POSITION_COMPLETE_TIMESTAMPS;
import static com.jaruiz.casarrubios.recruiters.services.newpospublisher.serdes.PositionSerdes.*;

@ApplicationScoped
public class NewPositionOpenPublishedTopology {

    private static final Logger logger = Logger.getLogger(NewPositionOpenPublishedTopology.class);

    @Inject TopologyConfig topologyConfig;

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        builder.addStateStore(
            Stores.keyValueStoreBuilder(
                Stores.inMemoryKeyValueStore(POSITION_COMPLETE_STORE),
                Serdes.Long(),
                positionCompleteSerde
            )
        );

        builder.addStateStore(
            Stores.keyValueStoreBuilder(
                Stores.inMemoryKeyValueStore(POSITION_COMPLETE_TIMESTAMPS),
                Serdes.Long(),
                Serdes.Long()
            )
        );

        // Streams de entrada
        KStream<Long, PositionTransactionElement> positionStream = builder.stream(topologyConfig.getCdcPositionsTopic(), Consumed.with(positionKeySerde, positionSerde))
            .selectKey((k, v) -> v.getId())
            .peek((k, v) -> { logger.info("Received new position: " + k.toString());})
            .mapValues((k, v) -> new PositionTransactionElement(v));

        KStream<Long, PositionTransactionElement> requirementStream = builder.stream(topologyConfig.getCdcPositionsRequirementsTopic(), Consumed.with(positionKeySerde, positionRequirementSerde))
            .selectKey((k, v) -> v.getPositionId())
            .peek((k, v) -> { logger.info("Received new requirement: " + k.toString());})
            .mapValues((k, v) -> new PositionTransactionElement(v));

        KStream<Long, PositionTransactionElement> taskStream = builder.stream(topologyConfig.getCdcPositionsTaskTopic(), Consumed.with(positionKeySerde, positionTaskSerde))
            .selectKey((k, v) -> v.getPositionId())
            .peek((k, v) -> { logger.info("Received new task: " + k.toString());})
            .mapValues((k, v) -> new PositionTransactionElement(v));

        KStream<Long, PositionTransactionElement> benefitStream = builder.stream(topologyConfig.getCdcPositionsBenefitsTopic(), Consumed.with(positionKeySerde, positionBenefitSerde))
            .selectKey((k, v) -> v.getPositionId())
            .peek((k, v) -> { logger.info("Received new benefit: " + k.toString());})
            .mapValues((k, v) -> new PositionTransactionElement(v));


        KStream<Long, PositionTransactionElement> mergedStream = positionStream
            .merge(requirementStream)
            .merge(taskStream)
            .merge(benefitStream);

       mergedStream.process(TimedPositionAggregatorProcessor::new,
           Named.as("position-aggregator"),
           POSITION_COMPLETE_STORE, POSITION_COMPLETE_TIMESTAMPS)
       .peek((k, v) -> { logger.info("Prepared to publish new position with key: " + k.toString());})
       .to(topologyConfig.getNewPositionsPublishedTopic(), Produced.with(Serdes.Long(), positionCompleteSerde));

        return builder.build();
    }
}
