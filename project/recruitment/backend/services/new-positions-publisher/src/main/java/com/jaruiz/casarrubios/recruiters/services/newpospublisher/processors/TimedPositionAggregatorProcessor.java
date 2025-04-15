package com.jaruiz.casarrubios.recruiters.services.newpospublisher.processors;

import java.time.Duration;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.PositionComplete;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.PositionTransactionElement;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.jboss.logging.Logger;
import static com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.PositionTransactionElement.*;

public class TimedPositionAggregatorProcessor implements Processor<Long, PositionTransactionElement, Long, PositionComplete> {
    private static final Logger logger = Logger.getLogger(TimedPositionAggregatorProcessor.class);

    public static final String POSITION_COMPLETE_STORE = "position-complete-store";
    public static final String POSITION_COMPLETE_TIMESTAMPS = "position-complete-timestamps";
    private ProcessorContext<Long, PositionComplete> context;
    private KeyValueStore<Long, PositionComplete> stateStore;
    private KeyValueStore<Long, Long> timestampStore;

    private static final Duration WINDOW = Duration.ofSeconds(5);

    @Override
    public void init(ProcessorContext<Long, PositionComplete> context) {
        this.context = context;
        this.stateStore = context.getStateStore(POSITION_COMPLETE_STORE);
        this.timestampStore = context.getStateStore(POSITION_COMPLETE_TIMESTAMPS);

        this.context.schedule(Duration.ofSeconds(1), PunctuationType.WALL_CLOCK_TIME, this::checkAndForward);
    }

    @Override
    public void process(Record<Long, PositionTransactionElement> record) {
        Long key = record.key();
        PositionTransactionElement element = record.value();
        logger.info("Processing record: " + key + " - " + element.getType());

        PositionComplete current = stateStore.get(key);
        if (current == null) {
            current = new PositionComplete();
        }

        switch (element.getType()) {
            case TYPE_POSITION -> current.addPosition(element.getPosition());
            case TYPE_REQUIREMENT -> current.addRequirement(element.getRequirement());
            case TYPE_TASK -> current.addTask(element.getTask());
            case TYPE_BENEFIT -> current.addBenefit(element.getBenefit());
        }

        stateStore.put(key, current);

        if (timestampStore.get(key) == null) {
            timestampStore.put(key, context.currentSystemTimeMs());
        }
    }

    private void checkAndForward(long timestamp) {
        try (KeyValueIterator<Long, PositionComplete> it = stateStore.all()) {
            while (it.hasNext()) {
                KeyValue<Long, PositionComplete> entry = it.next();
                Long key = entry.key;
                PositionComplete value = entry.value;

                Long firstSeen = timestampStore.get(key);
                if (firstSeen != null && isComplete(value) && isTimeToForward(firstSeen, timestamp)) {
                    context.forward(new Record<>(key, value, timestamp));
                    logger.info("Position completed and published [positionId = " + key + "]");
                    stateStore.delete(key);
                    timestampStore.delete(key);
                }
            }
        }
    }

    private boolean isTimeToForward(long firstSeen, long timestamp) {
        return (timestamp - firstSeen) >= WINDOW.toMillis();
    }

    private boolean isComplete(PositionComplete p) {
        return p.getId() != null &&
            p.getRequirements() != null && !p.getRequirements().isEmpty() &&
            p.getTasks() != null && !p.getTasks().isEmpty() &&
            p.getBenefits() != null && !p.getBenefits().isEmpty();
    }

    @Override
    public void close() {}
}
