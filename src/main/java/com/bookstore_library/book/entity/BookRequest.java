package com.bookstore_library.book.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class BookRequest {
    private String title;

    public String getTitle() {
        return title;
    }

    public BookRequest() {}

    public BookRequest(String title, String authorName, String isbn, String publishedDate) {
        this.title = title;
        this.authorName = authorName;
        this.isbn = isbn;
        this.publishedDate = publishedDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    private String authorName;
    private String isbn;
    private String publishedDate;
}

