package com.jaruiz.casarrubios.recruiters.services.globalposition.business.processors;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.jboss.logging.Logger;

public class AverageScoreProcessor implements Processor<String, String, Void, Void> {
    private static final Logger logger = Logger.getLogger(AverageScoreProcessor.class);

    private final String storeName;
    private KeyValueStore<String, Double> store;
    private long count = 0;
    private double sum = 0;

    public AverageScoreProcessor(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public void init(ProcessorContext<Void, Void> context) {
        store = context.getStateStore(storeName);
    }

    @Override
    public void process(Record<String, String> record) {
        logger.infof("Processing record: %s", record);
        try {
            String json = record.value();
            int idx = json.indexOf("\"score\":");
            if (idx > -1) {
                String after = json.substring(idx + 8).trim();
                String number = after.split("[,}]", 2)[0];
                double score = Double.parseDouble(number);
                count++;
                sum += score;
                double avg = sum / count;
                store.put("average", avg);
            }
        } catch (Exception e) {
            logger.error("Error parsing score");
            logger.error(e);
        }
    }

    @Override
    public void close() {}
}

