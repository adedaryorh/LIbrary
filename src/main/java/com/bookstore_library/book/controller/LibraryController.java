package com.bookstore_library.book.controller;

import com.bookstore_library.book.entity.Book;
import com.bookstore_library.book.entity.BorrowRequest;
import com.bookstore_library.book.entity.User;
import com.bookstore_library.book.repository.BookRepository;
import com.bookstore_library.book.service.BookService;
import com.bookstore_library.book.service.LibraryService;
import com.bookstore_library.book.service.UserService;
import com.bookstore_library.exceptions.BadRequestException;
import com.bookstore_library.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/library")
public class LibraryController {
    @Autowired
    private BookService bookService;
    @Autowired
    private BookRepository bookRepository;

    private final LibraryService libraryService;
    private final UserService userService;

    @Autowired
    public LibraryController(LibraryService libraryService, UserService userService) {
        this.libraryService = libraryService;
        this.userService = userService;
    }
    // Borrow a book
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody BorrowRequest request) {
        try {
            boolean success = libraryService.borrowBook(request.getUserId(), request.getBookId());
            if (!success) {
                return ResponseEntity.badRequest().body("Unable to borrow book. Book not available or user not found.");
            }
            return ResponseEntity.ok("Book borrowed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


    // Return a book
    @PostMapping("/return")
    public ResponseEntity<String> returnBook(@RequestParam String userId, @RequestParam Long bookId) {
        try {
            libraryService.returnBook(bookId, userId);
            return ResponseEntity.ok("Book returned successfully!");
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            libraryService.registerUser(user);
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

    // Get user details
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
    public ResponseEntity<String> addBook(@RequestBody Book book) {
        libraryService.addBook(book);
        return ResponseEntity.ok("Book added successfully!");
    }

    // Get all borrowed books by user
    @GetMapping("/user/{userId}/borrowed-books")
    public ResponseEntity<List<Book>> getUserBorrowedBooks(@PathVariable String userId) {
        try {
            List<Book> borrowedBooks = libraryService.getUserBorrowedBooks(userId);
            return ResponseEntity.ok(borrowedBooks);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Get the count of borrowed books by user
    @GetMapping("/user/{userId}/borrowed-books/count")
    public ResponseEntity<Integer> getUserBorrowedBookCount(@PathVariable String userId) {
        try {
            int count = libraryService.getUserBorrowedBookCount(userId);
            return ResponseEntity.ok(count);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
        }
    }

}

