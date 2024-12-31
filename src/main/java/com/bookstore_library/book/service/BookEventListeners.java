package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.Book;
import com.bookstore_library.book.entity.BookBorrowedEvent;
import com.bookstore_library.book.entity.BookReturnedEvent;
import com.bookstore_library.book.repository.BookRepository;
import com.bookstore_library.book.repository.UserRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
public class BookEventListeners {
    private static final Logger LOGGER = Logger.getLogger(BookEventListeners.class.getName());

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final Gson gson;

    @Autowired
    public BookEventListeners(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.gson = new Gson();
    }

    /**
     * Listens for BookBorrowedEvent from Kafka and updates the book and user records.
     *
     * @param message Kafka message containing BookBorrowedEvent.
     */
    @KafkaListener(topics = "book_borrowed_events", groupId = "library-group")
    @Transactional
    public void handleBookBorrowedEvent(String message) {
        BookBorrowedEvent event = gson.fromJson(message, BookBorrowedEvent.class);

        Book book = bookRepository.findByIsbn(event.getIsbn())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        userRepository.findById(event.getUserId())
                .ifPresent(user -> {
                    if (!book.isBorrowed()) {
                        book.setBorrowed(true);
                        user.getBorrowedBooks().add(book);
                        bookRepository.save(book);
                        userRepository.save(user);
                        LOGGER.info("Processed BookBorrowedEvent for book: " + book.getTitle());
                    } else {
                        LOGGER.warning("Book already borrowed: " + book.getTitle());
                    }
                });
    }

    /**
     * Listens for BookReturnedEvent from Kafka and updates the book and user records.
     *
     * @param message Kafka message containing BookReturnedEvent.
     */
    @KafkaListener(topics = "book_returned_events", groupId = "library-group")
    @Transactional
    public void handleBookReturnedEvent(String message) {
        BookReturnedEvent event = gson.fromJson(message, BookReturnedEvent.class);

        Book book = bookRepository.findByIsbn(event.getIsbn())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        userRepository.findById(event.getUserId())
                .ifPresent(user -> {
                    if (book.isBorrowed()) {
                        book.setBorrowed(false);
                        user.getBorrowedBooks().remove(book);
                        bookRepository.save(book);
                        userRepository.save(user);
                        LOGGER.info("Processed BookReturnedEvent for book: " + book.getTitle());
                    } else {
                        LOGGER.warning("Attempted to return a book that wasn't borrowed: " + book.getTitle());
                    }
                });
    }
}
