package com.bookstoreapp.springboot.book_store_app.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long id) {
        super("Book with id "+ id + " not found");
    }
}
