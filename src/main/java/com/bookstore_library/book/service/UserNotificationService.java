package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.UserRegisteredEvent;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for handling user registration events and sending notifications.
 * Listens to the "user_events" topic for UserRegisteredEvent messages.
 */
public class UserNotificationService {
    private static final Logger LOGGER = Logger.getLogger(UserNotificationService.class.getName());
    private static final String USER_EVENTS_TOPIC = "user_events";
    private static final String CONSUMER_GROUP_ID = "user-notification-group";

    private final KafkaConsumer<String, String> consumer;
    private final Gson gson;

    /**
     * Constructor to initialize the Kafka consumer and Gson serializer.
     */
    public UserNotificationService() {
        this.gson = new Gson();
        this.consumer = new KafkaConsumer<>(getConsumerProps());
        this.consumer.subscribe(Collections.singleton(USER_EVENTS_TOPIC));
    }

    /**
     * Configures the Kafka consumer properties.
     *
     * @return Properties for the Kafka consumer.
     */
    private Properties getConsumerProps() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Ensure consumption from the beginning if no offsets are stored
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");  // Disable auto-commit for better control
        return props;
    }

    /**
     * Starts listening to the Kafka topic and processes UserRegisteredEvent messages.
     */
    public void start() {
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        // Deserialize the event
                        UserRegisteredEvent event = gson.fromJson(record.value(), UserRegisteredEvent.class);
                        LOGGER.info("Received UserRegisteredEvent: " + event);

                        // Send welcome notification to the user
                        sendWelcomeNotification(event);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error processing record: " + record.value(), e);
                    }
                }

                // Commit offsets manually after processing the batch
                consumer.commitSync();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in UserNotificationService", e);
        } finally {
            consumer.close();
        }
    }

    /**
     * Sends a welcome notification to the user.
     *
     * @param event The UserRegisteredEvent containing user details.
     */
    private void sendWelcomeNotification(UserRegisteredEvent event) {
        // Simulate sending a notification (e.g., email or SMS)
        LOGGER.info("Sending welcome notification to user: " + event.getName());
        // Replace with actual notification logic (e.g., email/SMS API integration)
    }
}
