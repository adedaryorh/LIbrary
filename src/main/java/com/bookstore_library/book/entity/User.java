package com.bookstore_library.book.entity;

import com.bookstore_library.config.MaxBorrowLimit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.*;

/**
 * Entity class representing a user in the library system.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private Long id;
    private String userId;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    /**
     * Many-to-Many relationship between User and Book.
     * A user can borrow multiple books, and a book can be borrowed by multiple users.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_borrowed_books", // Join table for the relationship
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    @MaxBorrowLimit(max = 5)  // Custom annotation to limit borrowing
    private List<Book> borrowedBooks = new ArrayList<>();

    /**
     * Adds a book to the user's borrowed books list.
     * @param book the book to add
     */
    public void addBorrowedBook(Book book) {
        borrowedBooks.add(book);
    }
    /**
     * Removes a book from the user's borrowed books list.
     * @param book the book to remove
     */
    public void removeBorrowedBook(Book book) {
        borrowedBooks.remove(book);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(List<Book> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }
}
