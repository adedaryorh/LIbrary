package com.bookstore_library.book.service;

import java.time.Duration;
import com.bookstore_library.book.entity.BookBorrowedEvent;
import com.bookstore_library.book.entity.BookReturnedEvent;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * Listens to book borrowed and returned events to update user borrowing history.
 */
public class UserBorrowingHistoryService {
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String GROUP_ID = "user-borrowing-history-group";
    private static final String BOOK_BORROWED_EVENTS_TOPIC = "book_borrowed_events";
    private static final String BOOK_RETURNED_EVENTS_TOPIC = "book_returned_events";

    private final KafkaConsumer<String, String> consumer;
    private final Gson gson;

    public UserBorrowingHistoryService() {
        this.consumer = new KafkaConsumer<>(getConsumerProps());
        this.gson = new Gson();
        subscribeToTopics();
    }

    private Properties getConsumerProps() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return props;
    }

    private void subscribeToTopics() {
        consumer.subscribe(Arrays.asList(BOOK_BORROWED_EVENTS_TOPIC, BOOK_RETURNED_EVENTS_TOPIC));
    }

    public void start() {
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));  // Recommended
            ;
            records.forEach(this::processRecord);
            consumer.commitSync();
        }
    }

    private void processRecord(ConsumerRecord<String, String> record) {
        switch (record.topic()) {
            case BOOK_BORROWED_EVENTS_TOPIC:
                processBookBorrowedEvent(record);
                break;
            case BOOK_RETURNED_EVENTS_TOPIC:
                processBookReturnedEvent(record);
                break;
            default:
                System.err.println("Unknown topic: " + record.topic());
        }
    }

    private void processBookBorrowedEvent(ConsumerRecord<String, String> record) {
        BookBorrowedEvent event = gson.fromJson(record.value(), BookBorrowedEvent.class);
        System.out.println("Received BookBorrowedEvent: " + event);
        // Update user borrowing history
    }

    private void processBookReturnedEvent(ConsumerRecord<String, String> record) {
        BookReturnedEvent event = gson.fromJson(record.value(), BookReturnedEvent.class);
        System.out.println("Received BookReturnedEvent: " + event);
        // Update user borrowing history
    }
}
