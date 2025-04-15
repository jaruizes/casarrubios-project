package com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.containers;

import java.util.List;
import java.util.Map;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.kafka.KafkaAdminHelper;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaContainer implements QuarkusTestResourceLifecycleManager {

    private static final ConfluentKafkaContainer KAFKA_CONTAINER = new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));

    @Override
    public Map<String, String> start() {
        KAFKA_CONTAINER.start();

        waitForKafka(KAFKA_CONTAINER.getBootstrapServers());

        return Map.of(
            "kafka.bootstrap.servers", KAFKA_CONTAINER.getBootstrapServers(),
            "quarkus.kafka-streams.bootstrap-servers", KAFKA_CONTAINER.getBootstrapServers()
        );
    }

    @Override
    public void stop() {
        KAFKA_CONTAINER.stop();
    }

    private void waitForKafka(String bootstrapServers) {
        int retries = 10;
        while (retries-- > 0) {
            try {
                KafkaAdminHelper.createTopics(bootstrapServers, List.of("test-topic"));
                return;
            } catch (Exception e) {
                try {
                    Thread.sleep(2000); // Espera 2 segundos antes de reintentar
                } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("Kafka no está disponible después de varios intentos");
    }

    public static ConfluentKafkaContainer getKafkaContainer() {
        return KAFKA_CONTAINER;
    }
}
