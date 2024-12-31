package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.Book;
import com.bookstore_library.book.entity.BookAddedEvent;
import com.bookstore_library.book.entity.BookBorrowedEvent;
import com.bookstore_library.book.entity.BookReturnedEvent;
import com.bookstore_library.book.entity.User;
import com.bookstore_library.book.repository.BookRepository;
import com.bookstore_library.book.repository.UserRepository;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class LibraryService {
    private static final Logger LOGGER = Logger.getLogger(LibraryService.class.getName());
    private static final String BOOK_EVENTS_TOPIC = "book_events";
    private static final String BOOK_BORROWED_EVENTS_TOPIC = "book_borrowed_events";
    private static final String BOOK_RETURNED_EVENTS_TOPIC = "book_returned_events";

    private final KafkaProducer<String, String> producer;
    private final Gson gson;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public LibraryService(KafkaProducer<String, String> producer,
                          BookRepository bookRepository,
                          UserRepository userRepository) {
        this.producer = producer;
        this.gson = new Gson();
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Adds a book to the library and publishes a BookAddedEvent to Kafka.
     *
     * @param book The book to add.
     */
    public void addBook(Book book) {
        try {
            bookRepository.save(book);  // Save book to DB
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
     * @param bookId The ID of the book being borrowed.
     * @param userId The ID of the user borrowing the book.
     */
    public void borrowBook(Long bookId, String userId) {
        try {
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            Optional<User> userOpt = userRepository.findById(userId);

            if (bookOpt.isPresent() && userOpt.isPresent()) {
                Book book = bookOpt.get();
                User user = userOpt.get();

                if (!book.isBorrowed()) {
                    book.setBorrowed(true);
                    user.getBorrowedBooks().add(book);

                    bookRepository.save(book);
                    userRepository.save(user);

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
                } else {
                    LOGGER.warning("Book is already borrowed.");
                }
            } else {
                LOGGER.warning("Book or User not found.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing borrowBook", e);
        }
    }

    /**
     * Publishes a BookReturnedEvent when a user returns a book.
     *
     * @param bookId The ID of the book being returned.
     * @param userId The ID of the user returning the book.
     */
    public void returnBook(Long bookId, String userId) {
        try {
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            Optional<User> userOpt = userRepository.findById(userId);

            if (bookOpt.isPresent() && userOpt.isPresent()) {
                Book book = bookOpt.get();
                User user = userOpt.get();

                if (book.isBorrowed()) {
                    book.setBorrowed(false);
                    user.getBorrowedBooks().remove(book);

                    bookRepository.save(book);
                    userRepository.save(user);

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
                } else {
                    LOGGER.warning("Book was not marked as borrowed.");
                }
            } else {
                LOGGER.warning("Book or User not found.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing returnBook", e);
        }
    }
}
