package com.bookstore_library.book.entity;

// UserRegisteredEvent.java
public class UserRegisteredEvent {
    private String userId;
    private String name;

    public UserRegisteredEvent(String userId, String name) {
        this.userId = userId;
        this.name = name;
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
}

