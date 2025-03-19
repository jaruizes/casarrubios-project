package com.jaruiz.casarrubios.recruiters.services.newpospublisher;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.config.TopologyConfig;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.jboss.logging.Logger;
import static com.jaruiz.casarrubios.recruiters.services.newpospublisher.serdes.PositionSerdes.*;

@ApplicationScoped
public class NewPositionOpenPublishedTopology {

    private static final Logger logger = Logger.getLogger(NewPositionOpenPublishedTopology.class);
    private static final Duration FIVE_SECONDS = Duration.ofSeconds(5);
    private static final Duration TWO_SECONDS = Duration.ofSeconds(2);

    @Inject TopologyConfig topologyConfig;

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<Long, Position> positionsStream = builder.stream(topologyConfig.getCdcPositionsTopic(), Consumed.with(Serdes.Long(), positionSerde))
                                                         .selectKey((k, v) -> v.getId());
        KStream<Long, PositionRequirement> positionRequirementStream = builder.stream(topologyConfig.getCdcPositionsRequirementsTopic(), Consumed.with(Serdes.Long(), positionRequirementSerde))
                                                                            .selectKey((k, v) -> v.getPositionId());
        KStream<Long, PositionBenefit> positionBenefitStream = builder.stream(topologyConfig.getCdcPositionsBenefitsTopic(), Consumed.with(Serdes.Long(), positionBenefitSerde))
                                                                        .selectKey((k, v) -> v.getPositionId());
        KStream<Long, PositionTask> positionTaskStream = builder.stream(topologyConfig.getCdcPositionsTaskTopic(), Consumed.with(Serdes.Long(), positionTaskSerde))
                                                                    .selectKey((k, v) -> v.getPositionId());

        KStream<Long, Position> aggregatedPositionRequirements = positionRequirementStream
            .groupByKey()
            .windowedBy(SlidingWindows.ofTimeDifferenceWithNoGrace(FIVE_SECONDS))
            .aggregate(
                Position::new,
                this::positionRequirementsAggregator,
                Materialized.with(Serdes.Long(), positionSerde)
            )
            .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
            .toStream()
            .selectKey((k, v) -> k.key());

        KStream<Long, Position> aggregatedPositionTasksTable = positionTaskStream
            .groupByKey()
            .windowedBy(SlidingWindows.ofTimeDifferenceWithNoGrace(FIVE_SECONDS))
            .aggregate(
                Position::new,
                this::positionTasksAggregator,
                Materialized.with(Serdes.Long(), positionSerde)
            )
            .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
            .toStream()
            .selectKey((k, v) -> k.key());;

        KStream<Long, Position> aggregatedPositionBenefitsTable = positionBenefitStream
            .groupByKey()
            .windowedBy(SlidingWindows.ofTimeDifferenceWithNoGrace(FIVE_SECONDS))
            .aggregate(
                Position::new,
                this::positionBenefitsAggregator,
                Materialized.with(Serdes.Long(), positionSerde)
            )
            .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
            .toStream()
            .selectKey((k, v) -> k.key());;


        positionsStream.join(aggregatedPositionRequirements,
            this::positionAndRequirementsJoiner,
            JoinWindows.ofTimeDifferenceWithNoGrace(TWO_SECONDS),
            StreamJoined.with(Serdes.Long(), positionSerde, positionSerde)
        ).join(aggregatedPositionTasksTable,
           this::positionAndTasksJoiner,
           JoinWindows.ofTimeDifferenceWithNoGrace(TWO_SECONDS),
           StreamJoined.with(Serdes.Long(), positionSerde, positionSerde)
        ).join(aggregatedPositionBenefitsTable,
           this::positionAndBenefitsJoiner,
           JoinWindows.ofTimeDifferenceWithNoGrace(TWO_SECONDS),
           StreamJoined.with(Serdes.Long(), positionSerde, positionSerde)
        )
        .filter((k, v) -> v.getRequirements() != null && v.getTasks() != null && v.getBenefits() != null)
        .peek((k, v) -> logger.info("Position complete with requirements, tasks or benefits: " + v.getId()))
        .to(topologyConfig.getNewPositionsPublishedTopic(), Produced.with(Serdes.Long(), positionSerde));;

        return builder.build();
    }

    private Position positionRequirementsAggregator(Long key, PositionRequirement positionRequirement, Position aggregatedPositionRequirements) {
        logger.info("Aggregated position requirements: " + positionRequirement.getId() + " for position: " + positionRequirement.getPositionId());
        aggregatedPositionRequirements.addRequirement(positionRequirement);
        return aggregatedPositionRequirements;
    }

    private Position positionTasksAggregator(Long key, PositionTask positionTask, Position aggregatedPositionTasks) {
        logger.info("Aggregated position tasks: " + positionTask.getDescription()  + " for position: " + positionTask.getPositionId());
        aggregatedPositionTasks.addTask(positionTask);
        return aggregatedPositionTasks;
    }

    private Position positionBenefitsAggregator(Long key, PositionBenefit positionBenefit, Position aggregatedPositionBenefits) {
        logger.info("Aggregated position benefits: " + positionBenefit.getDescription()  + " for position: " + positionBenefit.getPositionId());
        aggregatedPositionBenefits.addBenefit(positionBenefit);
        return aggregatedPositionBenefits;
    }

    private Position positionAndRequirementsJoiner(Position position, Position positionWithRequirements) {
        if (positionWithRequirements != null && positionWithRequirements.getRequirements() != null) {
            List<PositionRequirement> requirements = positionWithRequirements.getRequirements();
            logger.info("Aggregating requirements to position. Total " +requirements.size() + " requirements.");
            position.setRequirements(requirements);
        }
        return position;
    }

    private Position positionAndTasksJoiner(Position position, Position positionWithTasks) {
        if (positionWithTasks != null && positionWithTasks.getTasks() != null) {
            List<PositionTask> tasks = positionWithTasks.getTasks();
            logger.info("Aggregating tasks to position. Total " +tasks.size() + " tasks.");
            position.setTasks(tasks);
        }
        return position;
    }

    private Position positionAndBenefitsJoiner(Position position, Position positionWithBenefits) {
        if (positionWithBenefits != null && positionWithBenefits.getBenefits() != null) {
            List<PositionBenefit> benefits = positionWithBenefits.getBenefits();
            logger.info("Aggregating benefits to position. Total " +benefits.size() + " benefits.");
            position.setBenefits(benefits);
        }
        return position;
    }

    public Properties getKafkaStreamsConfig() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-kafka-streams-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        return props;
    }


}
