package com.bookstoreapp.springboot.book_store_app.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User with id "+ id + " not found");
    }

    public UserNotFoundException(String username) {
        super("User not found with username: " + username);
    }

}
