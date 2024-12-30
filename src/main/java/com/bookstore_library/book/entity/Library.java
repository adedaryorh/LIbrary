package com.bookstore_library.book.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Library {
    private final List<Book> books;
    private final List<User> users;

    private static final Logger LOGGER = Logger.getLogger(Library.class.getName());
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String BOOK_EVENTS_TOPIC = "book_events";
    private static final String USER_EVENTS_TOPIC = "user_events";

    private final KafkaProducer<String, String> producer;
    private final ObjectMapper objectMapper;

    // Constructor
    public Library() {
        this.books = new ArrayList<>();
        this.users = new ArrayList<>();
        this.producer = createKafkaProducer();
        this.objectMapper = new ObjectMapper();
    }

    // Create Kafka producer
    private KafkaProducer<String, String> createKafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    // Add a new book and produce BookAddedEvent
    public void addBook(Book book) {
        books.add(book);
        BookAddedEvent event = new BookAddedEvent(book.getIsbn(), book.getTitle(), book.getAuthor().toString());
        sendEventToKafka(BOOK_EVENTS_TOPIC, event);
    }

    // Register a new user and produce UserRegisteredEvent
    public void registerUser(User user) {
        users.add(user);
        UserRegisteredEvent event = new UserRegisteredEvent(user.getUserId(), user.getName());
        sendEventToKafka(USER_EVENTS_TOPIC, event);
    }

    // General method to send events to Kafka
    private void sendEventToKafka(String topic, Object event) {
        String jsonEvent = serializeEventToJson(event);
        if (jsonEvent == null) {
            LOGGER.severe("Failed to serialize event. Event not sent to Kafka.");
            return;
        }

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, jsonEvent);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                LOGGER.log(Level.SEVERE, "Error producing event to Kafka", exception);
            } else {
                LOGGER.info("Produced event to Kafka topic: " + metadata.topic() +
                        ", Partition: " + metadata.partition() +
                        ", Offset: " + metadata.offset());
            }
        });
    }

    // Serialize event to JSON using Jackson
    private String serializeEventToJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Error serializing event to JSON", e);
            return null;
        }
    }
}
