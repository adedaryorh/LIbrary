package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.BookBorrowedEvent;
import com.bookstore_library.book.entity.BookReturnedEvent;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.Arrays;
import java.util.logging.Logger;

@Service
public class BookAvailabilityService {
    private static final Logger LOGGER = Logger.getLogger(BookAvailabilityService.class.getName());
    private static final String BOOK_BORROWED_EVENTS_TOPIC = "book_borrowed_events";
    private static final String BOOK_RETURNED_EVENTS_TOPIC = "book_returned_events";

    private final KafkaConsumer<String, String> consumer;
    private final Gson gson;

    public BookAvailabilityService(KafkaConsumer<String, String> consumer) {
        this.consumer = consumer;
        this.gson = new Gson();
    }

    @PostConstruct
    public void subscribe() {
        consumer.subscribe(Arrays.asList(BOOK_BORROWED_EVENTS_TOPIC, BOOK_RETURNED_EVENTS_TOPIC));
        startPolling();
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
        switch (record.topic()) {
            case BOOK_BORROWED_EVENTS_TOPIC:
                processBookBorrowedEvent(record);
                break;
            case BOOK_RETURNED_EVENTS_TOPIC:
                processBookReturnedEvent(record);
                break;
            default:
                LOGGER.warning("Received message from unknown topic: " + record.topic());
        }
    }

    private void processBookBorrowedEvent(ConsumerRecord<String, String> record) {
        BookBorrowedEvent event = gson.fromJson(record.value(), BookBorrowedEvent.class);
        LOGGER.info("Updating availability for borrowed book with ISBN: " + event.getIsbn());
        // Implement logic to mark book as unavailable in inventory.
    }

    private void processBookReturnedEvent(ConsumerRecord<String, String> record) {
        BookReturnedEvent event = gson.fromJson(record.value(), BookReturnedEvent.class);
        LOGGER.info("Updating availability for returned book with ISBN: " + event.getIsbn());
        // Implement logic to mark book as available in inventory.
    }

    @PreDestroy
    public void shutdown() {
        consumer.close();
    }
}
