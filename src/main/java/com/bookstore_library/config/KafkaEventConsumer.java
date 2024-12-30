package com.bookstore_library.config;

// KafkaEventConsumer.java
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaEventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaEventConsumer.class);
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String BOOK_EVENTS_TOPIC = "book_events";
    private static final String USER_EVENTS_TOPIC = "user_events";

    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = createKafkaConsumer();
        subscribeToTopics(consumer);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                LOGGER.info("Received event from topic: {}", record.topic());
                LOGGER.info("Event key: {}", record.key());
                LOGGER.info("Event value: {}", record.value());
            }
            consumer.commitSync();
        }
    }

    private static KafkaConsumer<String, String> createKafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "event-consumer-group");

        return new KafkaConsumer<>(props);
    }

    private static void subscribeToTopics(KafkaConsumer<String, String> consumer) {
        consumer.subscribe(Collections.singleton(BOOK_EVENTS_TOPIC));
        consumer.subscribe(Collections.singleton(USER_EVENTS_TOPIC));
    }
}

