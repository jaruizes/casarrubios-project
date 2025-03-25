package com.jaruiz.casarrubios.recruiters.services.globalposition.business;

import com.jaruiz.casarrubios.recruiters.services.globalposition.business.processors.AverageScoreProcessor;
import com.jaruiz.casarrubios.recruiters.services.globalposition.business.processors.IncrementProcessor;
import com.jaruiz.casarrubios.recruiters.services.globalposition.infrastructure.config.config.TopologyConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.Stores;
import org.jboss.logging.Logger;

@ApplicationScoped
public class GlobalPositionTopology {
    private static final Logger logger = Logger.getLogger(GlobalPositionTopology.class);
    public static final String POSITIONS_STORE = "positions-store";
    public static final String APPLICATIONS_STORE = "applications-store";
    public static final String SCORING_STORE = "scoring-store";

    @Inject TopologyConfig config;

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        builder.addStateStore(Stores.keyValueStoreBuilder(Stores.inMemoryKeyValueStore(POSITIONS_STORE), Serdes.String(), Serdes.Long()));
        builder.addStateStore(Stores.keyValueStoreBuilder(Stores.inMemoryKeyValueStore(APPLICATIONS_STORE), Serdes.String(), Serdes.Long()));
        builder.addStateStore(Stores.keyValueStoreBuilder(Stores.inMemoryKeyValueStore(SCORING_STORE), Serdes.String(), Serdes.Double()));

        KStream<String, String> positionsStream = builder.stream(config.getNewPositionsTopic());
        positionsStream.process(() -> new IncrementProcessor(POSITIONS_STORE), POSITIONS_STORE);

        KStream<String, String> applicationsStream = builder.stream(config.getNewApplicationsTopic());
        applicationsStream.process(() -> new IncrementProcessor(APPLICATIONS_STORE), APPLICATIONS_STORE);

        KStream<String, String> scoredStream = builder.stream(config.getScoringTopic());
        scoredStream.process(() -> new AverageScoreProcessor(SCORING_STORE), SCORING_STORE);

        return builder.build();
    }

}
