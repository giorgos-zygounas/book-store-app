package com.bookstoreapp.springboot.book_store_app.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Username " + username + " is not available");
    }
}
