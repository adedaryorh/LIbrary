package com.bookstore_library.book.service;

// InventoryAuditService.java
import com.bookstore_library.book.entity.BookAddedEvent;

import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class InventoryAuditService {
    private KafkaConsumer<String, String> consumer;
    private final Gson gson = new Gson();

    public InventoryAuditService() {
        consumer = new KafkaConsumer<>(getConsumerProps());
        consumer.subscribe(Collections.singleton("book_events"));
    }

    private Properties getConsumerProps() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "inventory-audit-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return props;
    }

    public void start() {
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));  // Recommended
            for (ConsumerRecord<String, String> record : records) {
                BookAddedEvent event = gson.fromJson(record.value(), BookAddedEvent.class);
                System.out.println("Received BookAddedEvent: " + event);
                // Log inventory addition for auditing purposes
            }
            consumer.commitSync();
        }
    }
}

