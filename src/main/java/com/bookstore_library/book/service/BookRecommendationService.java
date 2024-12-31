package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.BookAddedEvent;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

@Service
public class BookRecommendationService {
    private static final Logger LOGGER = Logger.getLogger(BookRecommendationService.class.getName());
    private static final String BOOK_EVENTS_TOPIC = "book_events";

    private KafkaConsumer<String, String> consumer;
    private final Gson gson;

    public BookRecommendationService() {
        this.gson = new Gson();
        this.consumer = createKafkaConsumer();
    }

    @PostConstruct
    public void subscribe() {
        consumer.subscribe(Collections.singleton(BOOK_EVENTS_TOPIC));
        startPolling();
    }

    private KafkaConsumer<String, String> createKafkaConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "book-recommendation-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return new KafkaConsumer<>(props);
    }

    private void startPolling() {
        new Thread(() -> {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    BookAddedEvent event = gson.fromJson(record.value(), BookAddedEvent.class);
                    LOGGER.info("Processing book for recommendation: " + event.getTitle());
                }
                consumer.commitSync();
            }
        }).start();
    }

    @PreDestroy
    public void shutdown() {
        consumer.close();
    }
}
