package com.bookstore_library.book.controller;

import com.bookstore_library.book.entity.Book;
import com.bookstore_library.book.entity.User;
import com.bookstore_library.book.repository.BookRepository;
import com.bookstore_library.book.service.BookService;
import com.bookstore_library.book.service.UserService;
import com.bookstore_library.exceptions.BadRequestException;
import com.bookstore_library.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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

    // Register a new user
    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        userService.registerUser(user);
        return "User registered successfully!";
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

