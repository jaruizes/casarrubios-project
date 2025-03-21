package com.jaruiz.casarrubios.candidates.services.positions.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class Config {
    @Value("${new-positions-published.topic:recruiters.new-positions-published}")
    private String newPositionsPublishedTopic;

    public String getNewPositionsPublishedTopic() {
        return newPositionsPublishedTopic;
    }
}
