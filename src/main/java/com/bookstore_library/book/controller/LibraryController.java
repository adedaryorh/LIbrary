package com.bookstore_library.book.controller;

import com.bookstore_library.book.entity.Book;
import com.bookstore_library.book.entity.User;
import com.bookstore_library.book.repository.BookRepository;
import com.bookstore_library.book.service.BookService;
import com.bookstore_library.book.service.UserService;
import com.bookstore_library.exceptions.BadRequestException;
import com.bookstore_library.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/library")
public class LibraryController {
    @Autowired
    private BookService bookService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserService userService;

    // Borrow a book
    @PostMapping("/borrow")
    public String borrowBook(@RequestParam String userId, @RequestParam Long bookId) {
        if (userService.getUserById(userId) == null) {
            throw new BadRequestException("User does not exist.");
        }
        if (!bookService.borrowBook(userId, bookId)) {
            throw new BadRequestException("Book is not available.");
        }
        return "Book borrowed successfully!";
    }

    // Return a book
    @PostMapping("/return")
    public String returnBook(@RequestParam String userId, @RequestParam Long bookId) {
        if (userService.getUserById(userId) == null) {
            throw new BadRequestException("User does not exist.");
        }
        if (!bookService.returnBook(userId, bookId)) {
            throw new BadRequestException("This book was not borrowed.");
        }
        return "Book returned successfully!";
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            userService.registerUser(user);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully!");
            response.put("userId", user.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    @GetMapping("/user/{userId}")
    public User getUser(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found.");
        }
        return user;
    }

    // Add a new book
    @PostMapping("/addBook")
    public String addBook(@RequestBody Book book) {
        bookRepository.save(book);
        return "Book added successfully!";
    }
}

