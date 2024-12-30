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


//Listeners (Consumers):
//UserProfileService

// UserProfileService.java
public class UserProfileService {
    private KafkaConsumer<String, String> consumer;
    private final Gson gson = new Gson();

    public UserProfileService() {
        consumer = new KafkaConsumer<>(getConsumerProps());
        consumer.subscribe(Collections.singleton("user_events"));
    }

    private Properties getConsumerProps() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-profile-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return props;
    }

    public void start() {
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));  // Recommended
            for (ConsumerRecord<String, String> record : records) {
                UserRegisteredEvent event = gson.fromJson(record.value(), UserRegisteredEvent.class);
                System.out.println("Received UserRegisteredEvent: " + event);
                // Create user profile
            }
            consumer.commitSync();
        }
    }
}
