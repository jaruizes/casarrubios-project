package com.jaruiz.casarrubios.recruiters.services.util.containers;

import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaTestResource implements QuarkusTestResourceLifecycleManager {

    private static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));

    @Override
    public Map<String, String> start() {
        kafka.start();
        return Map.of(
            "kafka.bootstrap.servers", kafka.getBootstrapServers()
        );
    }

    @Override
    public void stop() {
        kafka.stop();
    }
}
