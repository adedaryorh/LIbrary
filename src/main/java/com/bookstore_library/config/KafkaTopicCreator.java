package com.bookstore_library.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class KafkaTopicCreator {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Kafka Bootstrap Server
        String bootstrapServer = "localhost:9092";

        // Create AdminClient
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServer);
        AdminClient adminClient = AdminClient.create(props);

        // Topics to create
        String[] topicNames = {"book_events", "user_events"};

        // Create topics
        for (String topicName : topicNames) {
            createTopic(adminClient, topicName);
        }

        // Verify topic creation
        verifyTopicCreation(adminClient, topicNames);

        // Close AdminClient
        adminClient.close();
    }

    private static void createTopic(AdminClient adminClient, String topicName) throws InterruptedException, ExecutionException {
        NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
        adminClient.createTopics(Collections.singleton(newTopic)).all().get(); // Block until topic creation is complete
        System.out.println("Topic created: " + topicName);
    }

    private static void verifyTopicCreation(AdminClient adminClient, String[] topicNames) throws ExecutionException, InterruptedException {
        // Fetch the existing topics
        Set<String> existingTopics = adminClient.listTopics().names().get(); // Using names() instead of topics()

        // Check if each topic exists
        for (String topicName : topicNames) {
            if (existingTopics.contains(topicName)) {
                System.out.println("Topic exists: " + topicName);
            } else {
                System.out.println("Topic does not exist: " + topicName);
            }
        }
    }
}