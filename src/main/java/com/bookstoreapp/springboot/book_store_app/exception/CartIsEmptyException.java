package com.bookstoreapp.springboot.book_store_app.exception;

public class CartIsEmptyException extends RuntimeException {
    public CartIsEmptyException() {
        super("Cart is empty!");
    }
}
