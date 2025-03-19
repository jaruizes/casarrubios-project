package com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicCollection;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KafkaAdminHelper {
    public static void createTopics(String bootstrapServers, List<String> topicNames) {
        try (AdminClient adminClient = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers))) {
            List<NewTopic> newTopics = new ArrayList<>();
            for (String topicName : topicNames) {
                newTopics.add(new NewTopic(topicName, 1, (short) 1));
            }

            CreateTopicsResult result = adminClient.createTopics(newTopics);
            result.all().get();

            List<String> topics = getTopics(bootstrapServers);
            topicNames.forEach(topicToBeCreated -> {
                assertTrue(topics.contains(topicToBeCreated));
            });

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error creating Kafka topics", e);
        }
    }

    public static List<String> getTopics(String bootstrapServers) {
        try (AdminClient adminClient = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers))) {
            return new ArrayList<>(adminClient.listTopics().names().get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error getting Kafka topics", e);
        }
    }

    public static void cleanTopics(String bootstrapServers, List<String> topicNames) {
        try (AdminClient adminClient = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers))) {
            TopicCollection topicsToClean = TopicCollection.ofTopicNames(topicNames);

            DeleteTopicsResult result = adminClient.deleteTopics(topicsToClean);
            result.all().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error deleting Kafka topics", e);
        }
    }
}
