package com.jaruiz.casarrubios.recruiters.services.globalposition.business.processors;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.jboss.logging.Logger;

public class IncrementProcessor implements Processor<String, String, Void, Void> {
    private static final Logger logger = Logger.getLogger(IncrementProcessor.class);

    private final String storeName;
    private KeyValueStore<String, Long> store;

    public IncrementProcessor(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public void init(ProcessorContext<Void, Void> context) {
        store = context.getStateStore(storeName);
    }

    @Override
    public void process(Record<String, String> record) {
        Long value = store.get("count");

        logger.infof("Processing record: %s", record);
        store.put("count", value == null ? 1L : value + 1);
    }

    @Override
    public void close() {
        logger.infof("Closing processor: %s", storeName);
    }
}
