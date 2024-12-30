package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.Book;
import com.bookstore_library.book.entity.BookAddedEvent;
import com.bookstore_library.book.entity.BookBorrowedEvent;
import com.bookstore_library.book.entity.BookReturnedEvent;
import com.bookstore_library.book.entity.User;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for handling library operations and publishing events to Kafka.
 * This includes adding books, borrowing books, and returning books.
 */
public class LibraryService {
    private static final Logger LOGGER = Logger.getLogger(LibraryService.class.getName());
    private static final String BOOK_EVENTS_TOPIC = "book_events";
    private static final String BOOK_BORROWED_EVENTS_TOPIC = "book_borrowed_events";
    private static final String BOOK_RETURNED_EVENTS_TOPIC = "book_returned_events";

    private final KafkaProducer<String, String> producer;
    private final Gson gson;

    /**
     * Constructor to initialize the Kafka producer and Gson serializer.
     */
    public LibraryService() {
        this.producer = new KafkaProducer<>(getProducerProps());
        this.gson = new Gson();
    }

    /**
     * Configures the Kafka producer properties.
     *
     * @return Properties for the Kafka producer.
     */
    private Properties getProducerProps() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    /**
     * Publishes a BookAddedEvent when a new book is added to the library.
     *
     * @param book The book being added.
     */
    public void addBook(Book book) {
        try {
            BookAddedEvent event = new BookAddedEvent(book.getIsbn(), book.getTitle(), book.getAuthor().toString());
            String jsonEvent = gson.toJson(event);

            ProducerRecord<String, String> record = new ProducerRecord<>(BOOK_EVENTS_TOPIC, jsonEvent);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    LOGGER.log(Level.SEVERE, "Error publishing BookAddedEvent", exception);
                } else {
                    LOGGER.info("Published BookAddedEvent to topic: " + metadata.topic());
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing addBook", e);
        }
    }

    /**
     * Publishes a BookBorrowedEvent when a user borrows a book.
     *
     * @param book The book being borrowed.
     * @param user The user borrowing the book.
     */
    public void borrowBook(Book book, User user) {
        try {
            BookBorrowedEvent event = new BookBorrowedEvent(book.getIsbn(), user.getUserId());
            String jsonEvent = gson.toJson(event);

            ProducerRecord<String, String> record = new ProducerRecord<>(BOOK_BORROWED_EVENTS_TOPIC, jsonEvent);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    LOGGER.log(Level.SEVERE, "Error publishing BookBorrowedEvent", exception);
                } else {
                    LOGGER.info("Published BookBorrowedEvent to topic: " + metadata.topic());
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing borrowBook", e);
        }
    }

    /**
     * Publishes a BookReturnedEvent when a user returns a book.
     *
     * @param book The book being returned.
     * @param user The user returning the book.
     */
    public void returnBook(Book book, User user) {
        try {
            BookReturnedEvent event = new BookReturnedEvent(book.getIsbn(), user.getUserId());
            String jsonEvent = gson.toJson(event);

            ProducerRecord<String, String> record = new ProducerRecord<>(BOOK_RETURNED_EVENTS_TOPIC, jsonEvent);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    LOGGER.log(Level.SEVERE, "Error publishing BookReturnedEvent", exception);
                } else {
                    LOGGER.info("Published BookReturnedEvent to topic: " + metadata.topic());
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing returnBook", e);
        }
    }
}
