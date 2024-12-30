package com.bookstore_library.book.controller;

import com.bookstore_library.book.entity.Author;
import com.bookstore_library.book.entity.Book;
import com.bookstore_library.book.repository.AuthorRepository;
import com.bookstore_library.book.repository.BookRepository;
import com.bookstore_library.exceptions.ResourceNotFoundException;
import com.bookstore_library.book.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/books")
public class BookController {

    @Autowired
    BookService bookService;

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookController(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }
    @GetMapping("/allbooks")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found with ID " + id);
        }
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found with ID " + id);
        }
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBookById(@PathVariable Long id, @RequestBody Book book) {
        Book existingBook = bookService.getBookById(id);
        if (existingBook == null) {
            throw new ResourceNotFoundException("Book not found with ID " + id);
        }
        book.setId(id);
        bookService.saveOrUpdateBook(book);
        return ResponseEntity.ok(book);
    }

    @PostMapping("/createBook")
    public ResponseEntity<?> createBook(@RequestBody Map<String, Object> payload) {
        try {
            String title = (String) payload.get("title");
            String isbn = ((String) payload.get("isbn")).replace("-", "");  // Remove dashes from ISBN
            Long authorId = payload.get("authorId") != null ? ((Number) payload.get("authorId")).longValue() : null;

            Author author;

            // Case 1: Use existing author by authorId
            if (authorId != null) {
                author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new RuntimeException("Author not found with ID: " + authorId));
            }
            // Case 2: Create a new author if authorId is not provided
            else if (payload.get("author") != null) {
                Map<String, Object> authorPayload = (Map<String, Object>) payload.get("author");
                String authorName = (String) authorPayload.get("name");

                // Check if author with same name exists
                author = authorRepository.findByName(authorName)
                        .orElseGet(() -> {
                            Author newAuthor = new Author(authorName);
                            return authorRepository.save(newAuthor);  // Persist new author here
                        });
            }
            // Error: No author data provided
            else {
                return ResponseEntity.badRequest().body("Author information is required");
            }

            // Check for existing book by ISBN
            if (bookRepository.existsByIsbn(isbn)) {
                return ResponseEntity.badRequest().body("Book with this ISBN already exists");
            }

            // Persist the book with the associated author
            Book book = new Book(title, author, isbn);
            bookRepository.save(book);

            return ResponseEntity.ok(book);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


}
