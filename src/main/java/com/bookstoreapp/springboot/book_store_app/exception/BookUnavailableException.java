package com.bookstoreapp.springboot.book_store_app.exception;

public class BookUnavailableException extends RuntimeException {
    public BookUnavailableException() {
        super("Book is not available at the moment");
    }
}
