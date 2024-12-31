package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.*;
import com.bookstore_library.book.repository.BookRepository;
import com.bookstore_library.book.repository.UserRepository;
import com.bookstore_library.exceptions.BadRequestException;
import com.bookstore_library.exceptions.ResourceNotFoundException;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class LibraryService {
    private static final Logger LOGGER = Logger.getLogger(LibraryService.class.getName());
    private static final String BOOK_EVENTS_TOPIC = "book_events";
    private static final String BOOK_BORROWED_EVENTS_TOPIC = "book_borrowed_events";
    private static final String BOOK_RETURNED_EVENTS_TOPIC = "book_returned_events";
    private static final String USER_EVENTS_TOPIC = "user_events";  // Added this line

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final KafkaProducer<String, String> producer;
    private final Gson gson = new Gson();

    @Autowired
    public LibraryService(BookRepository bookRepository, UserRepository userRepository, KafkaProducer<String, String> producer) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.producer = producer;
    }

    @Transactional
    public void addBook(Book book) {
        bookRepository.save(book);
        BookAddedEvent event = new BookAddedEvent(book.getTitle(), book.getAuthor().toString(), book.getIsbn());
        publishEvent(BOOK_EVENTS_TOPIC, event);
    }

    @Transactional
    public boolean borrowBook(String userId, Long bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            LOGGER.warning("Borrow attempt failed - Book not found.");
            return false;
        }
        if (book.getBorrowCount() >= 5) {
            LOGGER.warning("Borrow attempt failed - Book has reached its borrow limit.");
            throw new BadRequestException("This book has reached its maximum borrow limit.");
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            LOGGER.warning("Borrow attempt failed - User not found.");
            return false;
        }
        book.setBorrowCount(book.getBorrowCount() + 1);  // Increment borrow count
        user.getBorrowedBooks().add(book);
        bookRepository.save(book);
        userRepository.save(user);

        BookBorrowedEvent event = new BookBorrowedEvent(book.getIsbn(), user.getUserId());
        publishEvent(BOOK_BORROWED_EVENTS_TOPIC, event);
        return true;
    }

    @Transactional
    public void returnBook(Long bookId, String userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!book.isBorrowed()) {
            throw new BadRequestException("Book is not borrowed");
        }
        book.setBorrowed(false);
        user.getBorrowedBooks().remove(book);
        bookRepository.save(book);
        userRepository.save(user);

        BookReturnedEvent event = new BookReturnedEvent(book.getIsbn(), user.getUserId());
        publishEvent(BOOK_RETURNED_EVENTS_TOPIC, event);
    }


    public List<Book> getUserBorrowedBooks(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getBorrowedBooks();
    }

    public int getUserBorrowedBookCount(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getBorrowedBooks().size();
    }

    @Transactional
    public void registerUser(User user) {
        if (userRepository.findById(user.getUserId()).isPresent()) {
            throw new BadRequestException("User already exists");
        }
        userRepository.save(user);
        UserRegisteredEvent event = new UserRegisteredEvent(user.getUserId(), user.getName());
        publishEvent(USER_EVENTS_TOPIC, event);
    }

    private void publishEvent(String topic, Object event) {
        try {
            String jsonEvent = gson.toJson(event);
            producer.send(new ProducerRecord<>(topic, jsonEvent));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to publish event to Kafka", e);
        }
    }
}
