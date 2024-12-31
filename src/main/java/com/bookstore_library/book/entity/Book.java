package com.bookstore_library.book.entity;

import com.bookstore_library.book.customs.ValidateISBN;
import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Column(unique = true, nullable = false)
    @ValidateISBN
    private String isbn;

    private boolean isBorrowed;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int borrowCount = 0;

    public Book(Long id, String title, Author author, String isbn, boolean isBorrowed, int borrowCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isBorrowed = isBorrowed;
        this.borrowCount = borrowCount;
    }

    public Book() {}

    public Book(String title, Author author, String isbn) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public void setBorrowed(boolean borrowed) {
        isBorrowed = borrowed;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }
}
