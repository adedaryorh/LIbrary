package com.bookstore_library.book.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookAddedEvent {
    @JsonProperty("title")
    private String title;
    @JsonProperty("author")
    private String authorName;
    @JsonProperty("isbn")
    private String isbn;

    public BookAddedEvent(String title, String authorName, String isbn) {
        this.title = title;
        this.authorName = authorName;
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
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
}
