package com.bookstoreapp.springboot.book_store_app.exception;

public class UserHasOrdersException extends RuntimeException {
    public UserHasOrdersException() {
        super("User has orders and cannot be deleted");
    }
}
