package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.Book;
import com.bookstore_library.book.entity.BookBorrowedEvent;
import com.bookstore_library.book.entity.BookReturnedEvent;
import com.bookstore_library.book.entity.User;
import com.bookstore_library.book.repository.BookRepository;
import com.bookstore_library.book.repository.UserRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.kafka.annotation.*;
import java.util.Optional;


@Service
public class BookEventListeners {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private final Gson gson = new Gson();

    @KafkaListener(topics = "book_borrowed_events", groupId = "library-group")
    @Transactional
    public void handleBookBorrowedEvent(String message) {
        BookBorrowedEvent event = gson.fromJson(message, BookBorrowedEvent.class);

        User user = userRepository.findById(event.getUserId()).orElseThrow(() ->
                new IllegalArgumentException("User not found"));
        Book book = bookRepository.findByIsbn(event.getIsbn()).orElseThrow(() ->
                new IllegalArgumentException("Book not found"));

        if (!book.isBorrowed()) {
            book.setBorrowed(true);
            user.getBorrowedBooks().add(book);
            bookRepository.save(book);
            userRepository.save(user);
        }
    }

    @KafkaListener(topics = "book_returned_events", groupId = "library-group")
    @Transactional
    public void handleBookReturnedEvent(String message) {
        BookReturnedEvent event = gson.fromJson(message, BookReturnedEvent.class);

        User user = userRepository.findById(event.getUserId()).orElseThrow(() ->
                new IllegalArgumentException("User not found"));
        Book book = bookRepository.findByIsbn(event.getIsbn()).orElseThrow(() ->
                new IllegalArgumentException("Book not found"));

        if (book.isBorrowed()) {
            book.setBorrowed(false);
            if (!user.getBorrowedBooks().remove(book)) {
                // Handle or log the scenario where the book is not in the user's borrowed books
                // For demonstration, logging the event
                System.out.println("Warning: Book not found in user's borrowed books during return event.");
            }
            bookRepository.save(book);
            userRepository.save(user);
        }
    }
}
