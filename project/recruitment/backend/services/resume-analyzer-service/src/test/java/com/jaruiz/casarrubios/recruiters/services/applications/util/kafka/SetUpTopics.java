package com.jaruiz.casarrubios.recruiters.services.applications.util.kafka;

import java.util.List;
import java.util.Properties;

import lombok.Getter;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.assertj.core.api.Assertions.fail;

@Component
public class SetUpTopics {
    private static final Logger logger = LoggerFactory.getLogger(SetUpTopics.class);
    public static final String APPLICATIONS_RECEIVED_TOPIC = "recruitment.applications-received";
    public static final String APPLICATIONS_ANALYSED_TOPIC = "recruitment.applications-analyzed";
    public static final String APPLICATIONS_DQL_TOPIC = "recruitment.applications-dlq";

    @Getter private static boolean created = false;

    public static void createKafkaTopics(String bootstrapServers) {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (AdminClient adminClient = AdminClient.create(config)) {
            List<NewTopic> topics = List.of(
                new NewTopic(APPLICATIONS_RECEIVED_TOPIC, 1, (short) 1),
                new NewTopic(APPLICATIONS_ANALYSED_TOPIC, 1, (short) 1),
                new NewTopic(APPLICATIONS_DQL_TOPIC, 1, (short) 1)
            );

            adminClient.createTopics(topics).all().whenComplete((v, e) -> {
                if (e != null) {
                    fail("Error creating topics", e);
                }
                logger.info("Topics created successfully!!!!!!!!!!!!!!!");
                created = true;
            });

        } catch (Exception e) {
            throw new RuntimeException("Error creando los topics de Kafka", e);
        }
    }
}
