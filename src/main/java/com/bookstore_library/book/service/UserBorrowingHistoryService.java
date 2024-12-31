package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.BookBorrowedEvent;
import com.bookstore_library.book.entity.BookReturnedEvent;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

@Service
public class UserBorrowingHistoryService {
    private static final Logger LOGGER = Logger.getLogger(UserBorrowingHistoryService.class.getName());
    private KafkaConsumer<String, String> consumer;
    private final Gson gson;

    public UserBorrowingHistoryService() {
        this.gson = new Gson();
        this.consumer = createKafkaConsumer();
    }

    @PostConstruct
    public void subscribe() {
        consumer.subscribe(Arrays.asList("book_borrowed_events", "book_returned_events"));
        startPolling();
    }

    private KafkaConsumer<String, String> createKafkaConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "user-borrowing-history-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return new KafkaConsumer<>(props);
    }

    private void startPolling() {
        new Thread(() -> {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                records.forEach(this::processRecord);
                consumer.commitSync();
            }
        }).start();
    }

    private void processRecord(ConsumerRecord<String, String> record) {
        if ("book_borrowed_events".equals(record.topic())) {
            BookBorrowedEvent event = gson.fromJson(record.value(), BookBorrowedEvent.class);
            LOGGER.info("Processed borrow event for book: " + event.getIsbn());
        } else if ("book_returned_events".equals(record.topic())) {
            BookReturnedEvent event = gson.fromJson(record.value(), BookReturnedEvent.class);
            LOGGER.info("Processed return event for book: " + event.getIsbn());
        }
    }

    @PreDestroy
    public void shutdown() {
        consumer.close();
    }
}
