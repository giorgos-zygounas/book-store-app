package com.bookstoreapp.springboot.book_store_app.exception;

public class BookUnavailableException extends RuntimeException {
    public BookUnavailableException(String message) {
        super("Book is not available at the moment");
    }
}
